package com.example.memorygame.ui.dialogs

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun WinDialog(
    onRestart: () -> Unit,
    onGoToMainMenu: () -> Unit
) {
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
