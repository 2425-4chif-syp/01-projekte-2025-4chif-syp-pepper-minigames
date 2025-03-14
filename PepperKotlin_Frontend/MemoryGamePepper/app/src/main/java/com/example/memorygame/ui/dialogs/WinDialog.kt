package com.example.memorygame.ui.dialogs

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.memorygame.logic.ScoreManager

@Composable
fun WinDialog(
    scoreManager: ScoreManager,
    onRestart: () -> Unit,
    onGoToMainMenu: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},  // Verhindert das Schließen des Dialogs durch Tippen außerhalb
        title = { Text("\uD83C\uDF89 Gratulation! \uD83C\uDF89\n" +
                "Du hast ${scoreManager.currentScore} Punkte erreicht!\n") },
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
