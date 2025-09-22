package com.example.mmg.network

import android.util.Log
import com.example.mmg.dto.MmgDto
import com.example.mmg.dto.StepDto
import com.example.mmg.network.service.MmgApiService
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.logging.HttpLoggingInterceptor


class HttpInstance {
    companion object {
        private const val BACKEND_URL = "https://vm107.htl-leonding.ac.at/"

        private val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        private val apiService: MmgApiService = retrofit.create(MmgApiService::class.java)

        suspend fun fetchMmgDtos(): List<MmgDto>? =
            withContext(Dispatchers.IO) {
                try {
                    val response = apiService.getMmgDtos()
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        Log.e("API", "Error: ${response.code()} - ${response.message()}")
                        null
                    }
                } catch (e: Exception) {
                    Log.e("API", "Exception: ${e.message}", e)
                    null
                }
            }

        suspend fun fetchMmgSteps(id: Int): List<StepDto>? =
            withContext(Dispatchers.IO) {
                try {
                    val response = apiService.getSteps(id)
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        Log.e("API", "Error: ${response.code()} - ${response.message()}")
                        null
                    }
                } catch (e: Exception) {
                    Log.e("API", "Exception: ${e.message}", e)
                    null
                }
            }
    }
}
