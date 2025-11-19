package com.pepper.pep.mealplan.network.services

import com.pepper.pep.mealplan.dto.Meal
import com.pepper.pep.mealplan.dto.MealPlan
import retrofit2.Response
import retrofit2.http.*

interface MealPlanService {

    @GET("api/mealplans")
    suspend fun getMealPlans(): Response<List<MealPlan>>

    @GET("api/mealplans/{id}")
    suspend fun getMealPlan(@Path("id") id: Long): Response<MealPlan>

    @POST("api/mealplans")
    suspend fun createMealPlan(@Body mealPlan: MealPlan): Response<MealPlan>

    @PUT("api/mealplans/{id}")
    suspend fun updateMealPlan(@Path("id") id: Long, @Body mealPlan: MealPlan): Response<MealPlan>

    @DELETE("api/mealplans/{id}")
    suspend fun deleteMealPlan(@Path("id") id: Long): Response<Void>

    @GET("api/meals")
    suspend fun getMealsByDateRange(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<List<Meal>>

    @GET("api/meals/type/{type}")
    suspend fun getMealsByType(@Path("type") mealType: String): Response<List<Meal>>

    @GET("api/meals/{id}")
    suspend fun getMeal(@Path("id") id: Long): Response<Meal>

    @POST("api/meals")
    suspend fun createMeal(@Body meal: Meal): Response<Meal>

    @PUT("api/meals/{id}")
    suspend fun updateMeal(@Path("id") id: Long, @Body meal: Meal): Response<Meal>

    @DELETE("api/meals/{id}")
    suspend fun deleteMeal(@Path("id") id: Long): Response<Void>

    @GET("api/meals/today")
    suspend fun getTodaysMeals(): Response<List<Meal>>

    @GET("api/meals/week")
    suspend fun getWeeklyMeals(
        @Query("week_start") weekStart: String
    ): Response<List<Meal>>
}