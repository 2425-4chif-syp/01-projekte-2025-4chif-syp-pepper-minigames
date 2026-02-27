package com.pepper.mealplan.network.dto

data class OrderDto(
    val id: Int? = null,
    val date: String,
    val userId: Int,
    val menuId: Int,
    val dessertSelected: Int? = null,
    val selectedLunchId: Int? = null,
    val selectedDinnerId: Int? = null,
    val orderedAt: String? = null
)
