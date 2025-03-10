package com.example.memorygame.navigation

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.example.memorygame.ui.screens.GridSelectionScreen
import com.example.memorygame.ui.screens.HighScoresScreen
import com.example.memorygame.ui.screens.InstructionsScreen
import com.example.memorygame.ui.menu.MainMenuScreen
import com.example.memorygame.MemoryGameScreen

@Composable
fun Navigation(navController: NavHostController, textToSpeech: TextToSpeech) {
    NavHost(navController = navController, startDestination = "main_menu") {
        // Hauptmenü
        composable("main_menu") {
            MainMenuScreen(textToSpeech,navController)
        }

        // Grid-Auswahl
        composable("grid_selection") {
            GridSelectionScreen(textToSpeech ,navController)
        }

        // Spiel
        composable(
            route = "game_screen/{rows}/{columns}",
            arguments = listOf(
                navArgument("rows") { type = NavType.IntType },
                navArgument("columns") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val rows = backStackEntry.arguments?.getInt("rows") ?: 4
            val columns = backStackEntry.arguments?.getInt("columns") ?: 4
            MemoryGameScreen(textToSpeech,navController ,rows, columns)
        }

        // High Scores
        composable("high_scores") {
            HighScoresScreen()
        }

        // Spieleinleitung
        composable("instructions") {
            InstructionsScreen(
                textToSpeech = textToSpeech,
                navController = navController
            )
        }
    }
}
