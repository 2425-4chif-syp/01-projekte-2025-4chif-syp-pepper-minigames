package com.pepper.mealplan.network.dto

data class ApiFoodDto(
    val id: Int?,
    val name: String?,
    val type: String?,
    val picture: ApiPictureDto?
)
