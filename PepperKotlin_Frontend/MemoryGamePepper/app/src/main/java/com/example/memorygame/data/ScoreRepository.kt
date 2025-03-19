package com.example.memorygame.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    fun getScores(onResult: (List<ScoreRequest>?) -> Unit) {
        api.getScores().enqueue(object : Callback<List<ScoreRequest>> {
            override fun onResponse(call: Call<List<ScoreRequest>>, response: Response<List<ScoreRequest>>) {
                if (response.isSuccessful) {
                    println("✅ Score erfolgreich aus dem Backend geholt")
                    onResult(response.body())
                } else {
                    println("❌ Fehler beim Holen Scores aus dem backend ${response.code()} - ${response.errorBody()?.string()}")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<List<ScoreRequest>>, t: Throwable) {
                onResult(null) // ❌ API-Fehler behandeln
            }
        })
    }
}
