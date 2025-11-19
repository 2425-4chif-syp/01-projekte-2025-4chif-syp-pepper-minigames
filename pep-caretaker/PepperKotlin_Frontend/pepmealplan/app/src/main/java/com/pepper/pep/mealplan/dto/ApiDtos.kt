package com.pepper.pep.mealplan.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: T?,
    
    @SerializedName("error")
    val error: String?
)

data class ErrorResponse(
    @SerializedName("error")
    val error: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("code")
    val code: Int?
)

// Request DTOs for creating/updating
data class CreateMealPlanRequest(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("start_date")
    val startDate: String,
    
    @SerializedName("end_date")
    val endDate: String
)

data class CreateMealRequest(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("meal_type")
    val mealType: String,
    
    @SerializedName("date")
    val date: String,
    
    @SerializedName("time")
    val time: String?,
    
    @SerializedName("meal_plan_id")
    val mealPlanId: Long?
)