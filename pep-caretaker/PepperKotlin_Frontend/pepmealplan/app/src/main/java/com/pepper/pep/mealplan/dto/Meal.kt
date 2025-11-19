package com.pepper.pep.mealplan.dto

import com.google.gson.annotations.SerializedName

data class Meal(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("meal_type")
    val mealType: String, // breakfast, lunch, dinner, snack
    
    @SerializedName("date")
    val date: String,
    
    @SerializedName("time")
    val time: String?,
    
    @SerializedName("ingredients")
    val ingredients: List<Ingredient>?,
    
    @SerializedName("nutrition_info")
    val nutritionInfo: NutritionInfo?,
    
    @SerializedName("image_url")
    val imageUrl: String?,
    
    @SerializedName("preparation_time")
    val preparationTime: Int?, // in minutes
    
    @SerializedName("difficulty_level")
    val difficultyLevel: String?, // easy, medium, hard
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
)