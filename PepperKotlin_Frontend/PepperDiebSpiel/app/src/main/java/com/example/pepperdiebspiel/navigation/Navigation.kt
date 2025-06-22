package com.example.pepperdiebspiel.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pepperdiebspiel.GameGrid
import com.example.pepperdiebspiel.screens.DifficultySelectionScreen
import com.example.pepperdiebspiel.screens.GameOverScreen
import com.example.pepperdiebspiel.screens.InfoScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    var selectedDifficulty by remember { mutableStateOf("easy") }
    var selectedTheme by remember { mutableStateOf("classic") }

    NavHost(navController = navController, startDestination = "difficulty_selection") {
        composable("difficulty_selection") {
            DifficultySelectionScreen(
                navController = navController,
                onStartGame = { difficulty, theme ->
                    selectedDifficulty = difficulty
                    selectedTheme = theme
                    navController.navigate("game")
                }
            )
        }

        composable("game") {
            GameGrid(
                navController = navController,
                difficulty = selectedDifficulty,
                theme = selectedTheme
            )
        }

        composable("info") {
            InfoScreen(navController = navController)
        }
        composable("gameOver") {
            GameOverScreen(navController)
        }

    }
}
