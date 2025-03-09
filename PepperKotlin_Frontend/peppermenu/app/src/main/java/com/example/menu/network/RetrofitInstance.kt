package com.example.menu.network

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Body
import java.io.ByteArrayOutputStream

object RetrofitInstance {
    const val BASE_URL = "https://your-backend-url.com/"  // Deine Basis-URL

    val api: NetworkService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetworkService::class.java)
    }
}

interface NetworkService {
    @Multipart
    @POST("api/auth/face/verify")
    suspend fun sendPostRequest(@Part image: MultipartBody.Part): String

    @POST("chat")
    suspend fun sendPostRequestSmallTalk(@Body said: RequestBody): String
}

object ApiHelper {
    suspend fun sendPostRequest(imageBitMap: ImageBitmap): String {
        val bitmap: Bitmap = imageBitMap.asAndroidBitmap()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val requestBody = RequestBody.create("application/octet-stream".toMediaType(), byteArray)
        val multipartBody = MultipartBody.Part.createFormData("image", "image.png", requestBody)

        return RetrofitInstance.api.sendPostRequest(multipartBody)
    }

    suspend fun sendPostRequestSmallTalk(said: String): String {
        val requestBody = RequestBody.create("text/plain".toMediaType(), said)
        return RetrofitInstance.api.sendPostRequestSmallTalk(requestBody)
    }
}
