package com.pepper.mealplan.network.dto

data class PersonDto(
    val id: Int? = null,
    val firstname: String,
    val lastname: String,
    val dob: String? = null
)
