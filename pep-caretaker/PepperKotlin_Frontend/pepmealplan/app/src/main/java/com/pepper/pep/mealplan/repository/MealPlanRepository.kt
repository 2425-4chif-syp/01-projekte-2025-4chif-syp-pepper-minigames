package com.pepper.pep.mealplan.repository

import com.pepper.pep.mealplan.dto.*
import com.pepper.pep.mealplan.network.HttpInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class MealPlanRepository {
    
    private val mealPlanService = HttpInstance.mealPlanService
    
    // Meal Plan operations
    suspend fun getAllMealPlans(): Result<List<MealPlan>> = withContext(Dispatchers.IO) {
        try {
            val response = mealPlanService.getMealPlans()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch meal plans: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMealPlan(id: Long): Result<MealPlan> = withContext(Dispatchers.IO) {
        try {
            val response = mealPlanService.getMealPlan(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch meal plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createMealPlan(request: CreateMealPlanRequest): Result<MealPlan> = withContext(Dispatchers.IO) {
        try {
            // Convert request to MealPlan for API call
            val mealPlan = MealPlan(
                id = 0, // Will be assigned by backend
                name = request.name,
                description = request.description,
                startDate = request.startDate,
                endDate = request.endDate,
                meals = emptyList(),
                createdAt = null,
                updatedAt = null
            )
            
            val response = mealPlanService.createMealPlan(mealPlan)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create meal plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateMealPlan(id: Long, mealPlan: MealPlan): Result<MealPlan> = withContext(Dispatchers.IO) {
        try {
            val response = mealPlanService.updateMealPlan(id, mealPlan)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update meal plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteMealPlan(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = mealPlanService.deleteMealPlan(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete meal plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Meal operations
    suspend fun getMealsByDateRange(startDate: String, endDate: String): Result<List<Meal>> = withContext(Dispatchers.IO) {
        try {
            val response = mealPlanService.getMealsByDateRange(startDate, endDate)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch meals: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMealsByType(mealType: String): Result<List<Meal>> = withContext(Dispatchers.IO) {
        try {
            val response = mealPlanService.getMealsByType(mealType)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch meals by type: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTodaysMeals(): Result<List<Meal>> = withContext(Dispatchers.IO) {
        try {
            val response = mealPlanService.getTodaysMeals()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch today's meals: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getWeeklyMeals(weekStart: String): Result<List<Meal>> = withContext(Dispatchers.IO) {
        try {
            val response = mealPlanService.getWeeklyMeals(weekStart)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch weekly meals: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createMeal(request: CreateMealRequest): Result<Meal> = withContext(Dispatchers.IO) {
        try {
            // Convert request to Meal for API call
            val meal = Meal(
                id = 0, // Will be assigned by backend
                name = request.name,
                description = request.description,
                mealType = request.mealType,
                date = request.date,
                time = request.time,
                ingredients = null,
                nutritionInfo = null,
                imageUrl = null,
                preparationTime = null,
                difficultyLevel = null,
                createdAt = null,
                updatedAt = null
            )
            
            val response = mealPlanService.createMeal(meal)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create meal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMeal(id: Long): Result<Meal> = withContext(Dispatchers.IO) {
        try {
            val response = mealPlanService.getMeal(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch meal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateMeal(id: Long, meal: Meal): Result<Meal> = withContext(Dispatchers.IO) {
        try {
            val response = mealPlanService.updateMeal(id, meal)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update meal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteMeal(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = mealPlanService.deleteMeal(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete meal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}