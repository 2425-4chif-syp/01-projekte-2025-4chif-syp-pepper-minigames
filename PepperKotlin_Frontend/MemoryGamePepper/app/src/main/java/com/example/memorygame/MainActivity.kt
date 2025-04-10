package com.example.memorygame

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.memorygame.ui.menu.MainMenuScreen
import com.example.memorygame.ui.screens.*
import com.example.memorygame.ui.theme.MemoryGameTheme
import java.util.Locale
import com.example.memorygame.data.repository.IntentPersonProvider
import com.example.memorygame.data.repository.MockPersonProvider

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisiere TextToSpeech
        textToSpeech = TextToSpeech(this, this)

        val mockPersonProvider = MockPersonProvider()
        val personProvider = IntentPersonProvider(intent)
        val personIntent = mockPersonProvider.getPerson()


        setContent {
            MemoryGameTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main_menu") {
                    // Hauptmenü
                    composable("main_menu") {
                        MainMenuScreen(navController, personIntent)
                    }

                    // Grid-Auswahl
                    composable("grid_selection") {
                        GridSelectionScreen(navController)
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
                        MemoryGameScreen(navController ,rows, columns, personIntent)
                    }

                    // High Scores => 1 Just for Test
                    composable("high_scores") {
                        HighScoresScreen(personIntent.id)
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
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.GERMAN
        }
    }

    override fun onDestroy() {
        textToSpeech.shutdown()
        super.onDestroy()
    }
}
