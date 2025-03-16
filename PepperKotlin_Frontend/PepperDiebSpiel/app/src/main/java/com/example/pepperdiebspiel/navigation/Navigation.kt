package com.example.pepperdiebspiel.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pepperdiebspiel.GameGrid
import com.example.pepperdiebspiel.screens.DifficultySelectionScreen
import com.example.pepperdiebspiel.screens.DifficultySelectionScreen

@Composable
fun AppNavigation(navController: NavHostController, onDifficultySelected: (String) -> Unit) {
    NavHost(navController = navController, startDestination = "start") {
        // Schwierigkeitsauswahl-Bildschirm
        composable("start") {
            DifficultySelectionScreen(navController = navController, onDifficultySelected = onDifficultySelected)
        }
        // Schwierigkeitsauswahl-Bildschirm
        composable("difficulty_selection") {
            DifficultySelectionScreen(navController = navController, onDifficultySelected = onDifficultySelected)
        }
        // Spiel-Bildschirm
        composable("game") {
            GameGrid()
        }
    }
}
