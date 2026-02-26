package com.pepper.mealplan.network.dto

data class ApiMealPlanDto(
    val id: Int?,
    val weekNumber: Int?,
    val weekDay: Int?,
    val soup: ApiFoodDto?,
    val lunch1: ApiFoodDto?,
    val lunch2: ApiFoodDto?,
    val lunchDessert: ApiFoodDto?,
    val dinner1: ApiFoodDto?,
    val dinner2: ApiFoodDto?
)