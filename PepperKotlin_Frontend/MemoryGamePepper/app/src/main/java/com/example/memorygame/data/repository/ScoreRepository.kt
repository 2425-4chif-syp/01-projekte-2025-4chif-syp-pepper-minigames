package com.example.memorygame.data

import com.example.memorygame.data.remote.NetworkModule
import com.example.memorygame.data.remote.ScoreApi
import com.example.memorygame.data.remote.ScoreRequest

object ScoreRepository {
    private val api: ScoreApi = NetworkModule.retrofit.create(ScoreApi::class.java)

    suspend fun getScoresForPlayer(playerId: Long): List<PlayerScore> {
        return try {
            api.getScoresForPlayer(playerId)
        } catch (e: Exception) {
            println("⚠️ Fehler beim Laden: ${e.message}")
            emptyList()
        }
    }

    suspend fun sendScore(scoreRequest: ScoreRequest): Boolean {
        // 🧪 Logge den ScoreRequest als JSON (hilfreich für Diagnose)
        val gson = com.google.gson.Gson()
        println("📤 Sende ScoreRequest als JSON:\n${gson.toJson(scoreRequest)}")

        return try {
            val response = api.submitScore(scoreRequest)
            if (response.isSuccessful) {
                println("✅ Score erfolgreich gesendet")
                true
            } else {
                println("❌ Fehler beim Senden: ${response.code()} - ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            println("❌ Netzwerkfehler: ${e.message}")
            false
        }
    }

}

