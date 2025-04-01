package com.example.mmg.network.service

import com.example.mmg.dto.MmgDto
import retrofit2.http.GET

interface MmgApiService {
    @GET("/api/tagalongstories")
    suspend fun getMmgDtos(): List<MmgDto>
}