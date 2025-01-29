package com.example.memorygame.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun GridSelectionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Wähle ein Grid:", modifier = Modifier.padding(16.dp))
        listOf(
            "2x3" to Pair(2, 3),
            "2x4" to Pair(2, 4),
            "3x4" to Pair(3, 4),
            "4x4" to Pair(4, 4)
        ).forEach { (label, gridSize) ->
            Button(
                onClick = {
                    navController.navigate("game_screen/${gridSize.first}/${gridSize.second}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = label)
            }
        }
    }
}
