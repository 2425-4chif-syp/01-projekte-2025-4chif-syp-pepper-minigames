package com.example.memorygame.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.LaunchedEffect

@Composable
fun MainMenuScreen(textToSpeech:TextToSpeech,navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LaunchedEffect(Unit) {
            textToSpeech.speak(
                "Willkommen zu Memory",
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            )
        }
        Text("Memory Game", modifier = Modifier.padding(16.dp))

        // Spiel starten
        Button(
            onClick = { navController.navigate("grid_selection") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Spiel starten")
        }

        // High Scores anzeigen
        Button(
            onClick = { navController.navigate("high_scores") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "High Scores anzeigen")
        }

        // Spieleinleitung
        Button(
            onClick = { navController.navigate("instructions") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Spieleanleitung")
        }
    }
}
