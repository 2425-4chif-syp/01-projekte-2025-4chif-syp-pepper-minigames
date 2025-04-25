package com.example.memorygame.data.remote

import com.example.memorygame.data.model.PersonIntent
import retrofit2.http.GET
import retrofit2.http.Path

interface PersonApi {

    @GET("/api/person/{id}")
    suspend fun getPersonById(@Path("id") id: Long): PersonIntent

}