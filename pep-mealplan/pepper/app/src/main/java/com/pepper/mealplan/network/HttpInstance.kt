package com.pepper.mealplan.network

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class HttpInstance {
    companion object {
        val BACKEND_URL = "https://vm107.htl-leonding.ac.at/" // Use HTTPS if required

        // Create a trust manager that accepts all certificates (for development only!)
        private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        private val client = OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .apply {
                // Configure SSL to trust all certificates (ONLY for development!)
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                hostnameVerifier { _, _ -> true }
            }
            .build()

        suspend fun sendPostRequestImage(imageBitMap: ImageBitmap): String =
            withContext(Dispatchers.IO) {
                val url = BACKEND_URL + "api/auth/face/verify"

                val bitmap: Bitmap = imageBitMap.asAndroidBitmap()
                
                // Skaliere das Bild auf eine maximale Größe runter
                val maxWidth = 800
                val maxHeight = 600
                val scaledBitmap = if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
                    val ratio = minOf(
                        maxWidth.toFloat() / bitmap.width,
                        maxHeight.toFloat() / bitmap.height
                    )
                    val newWidth = (bitmap.width * ratio).toInt()
                    val newHeight = (bitmap.height * ratio).toInt()
                    Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                } else {
                    bitmap
                }

                val byteArrayOutputStream = ByteArrayOutputStream()
                // Verwende JPEG mit 70% Qualität statt PNG mit 100%
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                
                println("Image size: ${byteArray.size} bytes (${byteArray.size / 1024} KB)")

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