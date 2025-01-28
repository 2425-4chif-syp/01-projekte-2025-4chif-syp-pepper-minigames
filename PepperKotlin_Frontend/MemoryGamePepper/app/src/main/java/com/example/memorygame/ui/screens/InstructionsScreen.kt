package com.example.memorygame.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState

@Composable
fun InstructionsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Überschrift
        Text(
            text = "🌟 Willkommen im Memory-Spiel! 🌟",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF00796B), // Schönes Grün für Frische
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Einführungstext
        Text(
            text = "🎮 Memory ist ein lustiges und spannendes Spiel, bei dem du dein Gedächtnis testen kannst. " +
                    "Dein Ziel ist es, immer zwei gleiche Bilder zu finden. Aber aufgepasst! 👀 Die Karten liegen verdeckt! " +
                    "Du kannst sie umdrehen, aber nur zwei auf einmal.",
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Erklärung der Logik
        Text(
            text = "🃏 Wenn die beiden Karten gleich sind, hast du ein Paar gefunden! 🎉 " +
                    "Mach weiter und finde alle Paare. Aber wenn die Karten nicht übereinstimmen, werden sie wieder verdeckt. " +
                    "🌀 Merke dir, wo sie liegen, um später schneller zu sein!",
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Besonderheit des Spiels
        Text(
            text = "🌈 Das Besondere an diesem Memory-Spiel: Es spielt mit deinen eigenen Bildern! 🌟 " +
                    "Vielleicht entdeckst du Bilder von dir, deiner Familie oder von besonderen Momenten, die dich zum Lächeln bringen. 😊 " +
                    "Dieses Memory-Spiel ist ein kleines Abenteuer durch die schönsten Erinnerungen deines Lebens. 💖",
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Abschluss
        Text(
            text = "🏆 Kannst du alle Paare entdecken? 🧐 Viel Spaß beim Spielen und genieße die Reise durch deine Erinnerungen! ✨",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
    }
}
