package com.pepper.pep.mealplan.dto

import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("quantity")
    val quantity: Double,
    
    @SerializedName("unit")
    val unit: String, // g, kg, ml, l, piece, cup, etc.
    
    @SerializedName("category")
    val category: String?, // protein, vegetables, dairy, etc.
    
    @SerializedName("allergens")
    val allergens: List<String>?
)