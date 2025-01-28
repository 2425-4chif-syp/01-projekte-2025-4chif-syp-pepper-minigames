package com.example.memorygame.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.memorygame.ui.menu.MainMenuScreen
import com.example.memorygame.ui.screens.GridSelectionScreen
import com.example.memorygame.ui.screens.HighScoresScreen
import com.example.memorygame.ui.screens.InstructionsScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_menu") {
        composable("main_menu") { MainMenuScreen(navController) }
        composable("grid_selection") { GridSelectionScreen(navController) }
        composable("high_scores") { HighScoresScreen() }
        composable("instructions") { InstructionsScreen() }
    }
}
