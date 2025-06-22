package com.example.pepperdiebspiel.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun GameOverScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFB71C1C), Color(0xFF4A148C))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Zeit ist abgelaufen!", fontSize = 32.sp, color = Color.White)
            Text("Du hast den Dieb nicht rechtzeitig gefunden.", fontSize = 20.sp, color = Color.White)

            Button(
                onClick = {
                    navController.navigate("difficulty_selection") {
                        popUpTo("gameOver") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
            ) {
                Text("Zurück zum Start", color = Color.White)
            }
        }
    }
}
