package com.example.mmg.dto

import com.google.gson.annotations.SerializedName

data class StepDto(
    @SerializedName("index") val index: Int,
    @SerializedName("imageBase64") val imageBase64: String? = null,
    @SerializedName("move") val move: MoveDto? = null,
    @SerializedName("text") val text: String,
    @SerializedName("durationInSeconds") val durationInSeconds: Int
)

data class MoveDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)