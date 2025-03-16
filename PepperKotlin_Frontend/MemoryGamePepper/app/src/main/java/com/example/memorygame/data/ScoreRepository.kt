package com.example.memorygame.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScoreRepository {
    private val api: ScoreApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/") // Mock-URL für Tests
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ScoreApi::class.java)
    }

    fun sendScore(score: ScoreRequest, onResult: (Boolean) -> Unit) {
        api.sendScore(score).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("✅ Score erfolgreich ans Backend gesendet!")
                    onResult(true)
                } else {
                    println("❌ Fehler beim Senden des Scores: HTTP ${response.code()} - ${response.errorBody()?.string()}")
                    onResult(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("❌ API-Request fehlgeschlagen: ${t.message}")
                onResult(false)
            }
        })
    }

}
