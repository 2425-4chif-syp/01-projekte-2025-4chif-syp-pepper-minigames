package com.pepper.mealplan.network.dto

import com.google.gson.annotations.SerializedName

data class ExportOrderDto(
    @SerializedName("id") val id: Int,
    @SerializedName("person") val person: ResidentDto,
    @SerializedName("date") val date: String,
    @SerializedName("selectedLunch") val selectedLunch: ExportFoodDto? = null,
    @SerializedName("selectedDinner") val selectedDinner: ExportFoodDto? = null
)

data class ExportFoodDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("picture") val picture: ExportPictureDto? = null
)

data class ExportPictureDto(
    @SerializedName("id") val id: Int,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String?
)