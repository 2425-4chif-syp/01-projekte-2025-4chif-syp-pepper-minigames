package com.example.pepperdiebspiel.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pepperdiebspiel.GameGrid
import com.example.pepperdiebspiel.screens.StartScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "start") {
        // Start-Bildschirm
        composable("start") {
            StartScreen(navController = navController)
        }
        // Spiel-Bildschirm
        composable("game") {
            GameGrid()
        }
    }
}
