package com.example.pepperdiebspiel.screens

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun InfoScreen(navController: NavController) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spiel-Objekte") },
                backgroundColor = Color.Black,
                contentColor = Color.White
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF121212), Color(0xFF333333))
                    )
                )
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Alle Objekte im Spiel:",
                fontSize = 26.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            for (info in allGameInfos) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = info.imageRes),
                            contentDescription = info.description,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(end = 16.dp)
                        )
                        Text(
                            text = info.description,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            mediaPlayer?.release()
                            mediaPlayer = MediaPlayer.create(context, info.soundRes)
                            mediaPlayer?.start()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Sound abspielen",
                            tint = Color.White
                        )
                    }
                }
                Divider(color = Color.Gray, thickness = 1.dp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Spielanleitung:",
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = """
                In „Fang den Dieb!“ versteckt sich ein Dieb auf einem Spielfeld.
                Deine Aufgabe ist es, ihn zu finden, bevor er entkommt!

                • Der Dieb bewegt sich alle 4 Sekunden auf ein benachbartes Feld.
                • Tippe auf ein Feld, um dort zu suchen.
                • Jedes Objekt auf dem Spielfeld hat ein eigenes Geräusch.
                • Du kannst die Geräusche hier anhören, um sie im Spiel wiederzuerkennen.
                • Wenn du den Dieb findest, hast du gewonnen!

                Viel Spaß beim Jagen!
                """.trimIndent(),
                fontSize = 16.sp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    mediaPlayer?.release()
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Zurück", color = Color.White)
            }
        }
    }
}
