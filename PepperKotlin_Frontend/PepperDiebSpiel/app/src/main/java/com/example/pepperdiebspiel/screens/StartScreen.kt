package com.example.pepperdiebspiel.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pepperdiebspiel.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DifficultySelectionScreen(
    navController: NavController,
    onStartGame: (String, String) -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf("easy") }
    var selectedTheme by remember { mutableStateOf("classic") }

    Box(modifier = Modifier.fillMaxSize()) {

        // 🔹 Hintergrundbild
        Image(
            painter = painterResource(id = R.drawable.startscreen),
            contentDescription = "Hintergrund",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        FloatingActionButton(
            onClick = { navController.navigate("info") },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .size(56.dp),
            backgroundColor = Color.White.copy(alpha = 0.9f),
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Spiel-Info",
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = "🎩 Fang den Dieb!",
            fontSize = 36.sp,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .shadow(8.dp),
            letterSpacing = 1.5.sp
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {



            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(140.dp) // Breite fixieren wie die Buttons
                ) {
                    Text(
                        "Schwierigkeit",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DifficultyButton("Einfach", "easy", selectedDifficulty) { selectedDifficulty = it }
                    DifficultyButton("Mittel", "medium", selectedDifficulty) { selectedDifficulty = it }
                    DifficultyButton("Schwer", "hard", selectedDifficulty) { selectedDifficulty = it }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(140.dp)
                ) {
                    Text(
                        "Thema",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ThemeButton("Classic", "classic", selectedTheme) { selectedTheme = it }
                    ThemeButton("Grusel", "scary", selectedTheme) { selectedTheme = it }
                    ThemeButton("Nacht", "night", selectedTheme) { selectedTheme = it }
                    ThemeButton("Chaos", "chaos", selectedTheme) { selectedTheme = it }
                }

            }


            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    onStartGame(selectedDifficulty, selectedTheme)
                    navController.navigate("game")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF03A9F4)),
                modifier = Modifier
                    .height(60.dp)
                    .width(200.dp)
                    .shadow(6.dp, RoundedCornerShape(20.dp))
            ) {
                Text("Spiel starten", fontSize = 22.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun DifficultyButton(label: String, value: String, selected: String, onSelect: (String) -> Unit) {
    Button(
        onClick = { onSelect(value) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (selected == value) Color(0xFF4CAF50) else Color.DarkGray
        ),
        modifier = Modifier
            .padding(4.dp)
            .width(140.dp)
            .height(48.dp)
    ) {
        Text(label, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
fun ThemeButton(label: String, value: String, selected: String, onSelect: (String) -> Unit) {
    Button(
        onClick = { onSelect(value) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (selected == value) Color.Magenta else Color.DarkGray
        ),
        modifier = Modifier
            .padding(4.dp)
            .width(140.dp)
            .height(48.dp)
    ) {
        Text(label, fontSize = 16.sp, color = Color.White)
    }
}
