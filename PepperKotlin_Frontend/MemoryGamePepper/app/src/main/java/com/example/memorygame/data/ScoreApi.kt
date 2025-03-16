package com.example.memorygame.data

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

interface ScoreApi {
    @POST("posts") // Endpunkt für das Senden der Scores
    fun sendScore(@Body score: ScoreRequest): Call<Void> // Leere Antwort (Backend bestätigt nur)
}