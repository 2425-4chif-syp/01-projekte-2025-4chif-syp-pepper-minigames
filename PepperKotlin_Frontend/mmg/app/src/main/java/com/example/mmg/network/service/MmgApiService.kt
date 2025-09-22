package com.example.mmg.network.service

import com.example.mmg.dto.MmgDto
import com.example.mmg.dto.StepDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MmgApiService {
    @GET("api/tagalongstories")
    suspend fun getMmgDtos(): Response<List<MmgDto>>

    @GET("api/tagalongstories/{storyId}/steps")
    suspend fun getSteps(@Path("storyId") storyId: Int): Response<List<StepDto>>
}
