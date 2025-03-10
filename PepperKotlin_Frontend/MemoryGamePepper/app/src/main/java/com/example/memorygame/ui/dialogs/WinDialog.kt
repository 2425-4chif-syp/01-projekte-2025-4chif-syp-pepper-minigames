package com.example.memorygame.ui.dialogs

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.LaunchedEffect


@Composable
fun WinDialog(
    onRestart: () -> Unit,
    onGoToMainMenu: () -> Unit,
    textToSpeech: TextToSpeech
) {
    LaunchedEffect(Unit) {
        textToSpeech.speak(
            "Super! ois Richtig",
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )}
    AlertDialog(
        onDismissRequest = {},  // Verhindert das Schließen des Dialogs durch Tippen außerhalb
        title = { Text("Gratulation, du hast gewonnen!") },

        text = { Text("Möchtest du das Spiel neu starten oder zurück zum Hauptmenü gehen?") },
        confirmButton = {
            Button(onClick = onRestart) {
                Text("Neustart")
            }
        },
        dismissButton = {
            Button(onClick = onGoToMainMenu) {
                Text("Zurück zum Hauptmenü")
            }
        }
    )
}
