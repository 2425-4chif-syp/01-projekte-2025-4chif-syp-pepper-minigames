package com.example.mmg.network

import com.example.mmg.dto.MmgDto
import com.example.mmg.network.service.MmgApiService
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpInstance {
    companion object{
        val BACKEND_URL = "http://vm88.htl-leonding.ac.at:8080/"

        // Client-Objekt einmal erstellen und wiederverwenden
        private val client = OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val apiService: MmgApiService = retrofit.create(MmgApiService::class.java)

        suspend fun fetchMmgDtos(): List<MmgDto>? {
            return withContext(Dispatchers.IO) {
                try {
                    apiService.getMmgDtos()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

}