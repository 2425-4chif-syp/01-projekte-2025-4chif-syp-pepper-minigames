package com.pepper.mealplan.network

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class HttpInstance {
    companion object {
        val BACKEND_URL = "http://vm107.htl-leonding.ac.at/"

        private val client = OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()

        suspend fun sendPostRequestImage(imageBitMap: ImageBitmap): String =
            withContext(Dispatchers.IO) {
                val url = BACKEND_URL + "api/auth/face/verify"

                val bitmap: Bitmap = imageBitMap.asAndroidBitmap()
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                val requestBody = RequestBody.create(
                    "application/octet-stream".toMediaTypeOrNull(),
                    byteArray
                )

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                try {
                    val response: Response = client.newCall(request).execute()
                    val responseBody = response.body?.string()

                    println("Status Code: ${response.code}")
                    println("Response Body: $responseBody")

                    if (responseBody.isNullOrEmpty()) {
                        println("Error: Response is empty or null!")
                        ""
                    } else {
                        responseBody
                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                    e.printStackTrace()
                    ""
                }
            }
    }
}