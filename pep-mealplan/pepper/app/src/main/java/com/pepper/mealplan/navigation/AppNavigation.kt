package com.pepper.mealplan.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pepper.mealplan.MealPlanOverview.MealPlanOverview
import com.pepper.mealplan.createMealPlan.CreateMealPlan

@Composable
fun AppNavigation(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = "overview"
    )
    {
        composable("overview"){MealPlanOverview()}
        composable("create"){ CreateMealPlan()}
    }
}