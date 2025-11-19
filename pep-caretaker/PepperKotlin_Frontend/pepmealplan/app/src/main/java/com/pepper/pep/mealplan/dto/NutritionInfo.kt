package com.pepper.pep.mealplan.dto

import com.google.gson.annotations.SerializedName

data class NutritionInfo(
    @SerializedName("calories")
    val calories: Double,
    
    @SerializedName("protein")
    val protein: Double, // in grams
    
    @SerializedName("carbohydrates")
    val carbohydrates: Double, // in grams
    
    @SerializedName("fat")
    val fat: Double, // in grams
    
    @SerializedName("fiber")
    val fiber: Double?, // in grams
    
    @SerializedName("sugar")
    val sugar: Double?, // in grams
    
    @SerializedName("sodium")
    val sodium: Double?, // in mg
    
    @SerializedName("cholesterol")
    val cholesterol: Double? // in mg
)