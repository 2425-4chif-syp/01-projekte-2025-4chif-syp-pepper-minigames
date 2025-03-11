package com.example.menu.network

import android.graphics.Bitmap
import android.util.Log
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
        val BACKEND_URL = "http://vm88.htl-leonding.ac.at:8080/"

        // Client-Objekt einmal erstellen und wiederverwenden
        private val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()

        // Als suspend-Funktion markieren und withContext verwenden
        suspend fun sendPostRequestImage(imageBitMap: ImageBitmap): String = withContext(Dispatchers.IO) {
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

        suspend fun sendPostRequestSmallTalk(said: String): String = withContext(Dispatchers.IO) {
            val url = "$BACKEND_URL/chat"
            Log.d("Status", "Status Code:")

            val requestBody = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                said
            )

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d("Status", "Status Code: ${response.code}")
                Log.d("Response", "Response Body: $responseBody")

                if (responseBody.isNullOrEmpty()) {
                    Log.d("Error", "Response is empty or null!")
                    ""
                } else {
                    responseBody
                }
            } catch (e: Exception) {
                Log.e("Error", "Exception: ${e.message}")
                e.printStackTrace()
                ""
            }
        }
    }
}