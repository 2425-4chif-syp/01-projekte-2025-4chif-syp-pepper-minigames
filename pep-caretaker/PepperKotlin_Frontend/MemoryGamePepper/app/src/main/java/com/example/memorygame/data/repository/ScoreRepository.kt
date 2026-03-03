package com.example.memorygame.data.repository

import com.example.memorygame.data.model.PlayerScore
import com.example.memorygame.data.remote.NetworkModule
import com.example.memorygame.data.remote.ScoreApi
import com.example.memorygame.data.remote.ScoreRequest

object ScoreRepository {
    private val api: ScoreApi = NetworkModule.provideScoreApi()

    suspend fun getScoresForPlayer(playerId: Long): List<PlayerScore> {
        return try {
            api.getScoresForPlayer(playerId)
        } catch (e: Exception) {
            println("⚠️ Fehler beim Laden: ${e.message}")
            emptyList()
        }
    }

    suspend fun sendScore(scoreRequest: ScoreRequest): Boolean {
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

