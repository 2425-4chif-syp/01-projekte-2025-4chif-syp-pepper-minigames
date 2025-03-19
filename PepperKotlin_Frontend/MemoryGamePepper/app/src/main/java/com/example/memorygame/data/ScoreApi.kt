package com.example.memorygame.data

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.GET

interface ScoreApi {
    @POST("posts") //"scores" wenn Backend fertig ist
    fun sendScore(@Body score: ScoreRequest): Call<Void>

    @GET("scores")
    fun getScores(): Call<List<ScoreRequest>>

}