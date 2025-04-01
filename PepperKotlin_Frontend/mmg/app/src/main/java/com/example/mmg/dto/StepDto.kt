package com.example.mmg.dto

import com.google.gson.annotations.SerializedName

data class StepDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("game") val game: GameDto? = null,
    @SerializedName("index") val index: Int,
    @SerializedName("imageBase64") val imageBase64: String? = null,
    @SerializedName("imageType") val imageType: String? = null,
    @SerializedName("move") val move: MoveDto? = null,
    @SerializedName("text") val text: String,
    @SerializedName("durationInSeconds") val durationInSeconds: Int
)

data class GameDto(
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("gameType") val gameType: GameTypeDto,
    @SerializedName("enabled") val enabled: Boolean
)

data class GameTypeDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class MoveDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)