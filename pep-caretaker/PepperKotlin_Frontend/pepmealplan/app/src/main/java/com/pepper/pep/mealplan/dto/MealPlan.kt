package com.pepper.pep.mealplan.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class MealPlan(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("start_date")
    val startDate: String,
    
    @SerializedName("end_date")
    val endDate: String,
    
    @SerializedName("meals")
    val meals: List<Meal>,
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
)