package com.example.memorygame.ui.menu

import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun MainMenuScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Memory Game") })
        }
    ) { paddingValues -> // "paddingValues" ist das übergebene Content Padding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Padding wird hier verwendet
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { navController.navigate("grid_selection") }) {
                Text("Spiel starten")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("high_scores") }) {
                Text("High Scores anzeigen")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("instructions") }) {
                Text("Spielanleitung/Einstellungen")
            }
        }
    }
}
