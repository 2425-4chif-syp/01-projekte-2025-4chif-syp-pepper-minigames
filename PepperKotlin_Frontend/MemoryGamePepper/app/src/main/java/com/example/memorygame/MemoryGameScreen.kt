package com.example.memorygame

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.memorygame.logic.selectImagesForGrid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoryGameScreen(rows: Int, columns: Int) {
    // Anzahl der benötigten Paare berechnen
    val requiredPairs = (rows * columns) / 2

    // Benutzerbilder und lokale Bilder
    var userImages by remember { mutableStateOf<List<Int>?>(null) }
    val localImages = listOf(
        R.drawable.local1, R.drawable.local2, R.drawable.local3, R.drawable.local4,
        R.drawable.local5, R.drawable.local6, R.drawable.local7, R.drawable.local8
    )

    // Coroutine für Datenabruf simulieren (Backend-Aufruf)
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // Simulierter API-Aufruf, um Benutzerbilder zu laden
                userImages = fetchUserImagesFromBackend()
            } catch (e: Exception) {
                Log.e("MemoryGame", "Fehler beim Laden der Benutzerbilder: ${e.message}")
                userImages = emptyList() // Fallback auf keine Benutzerbilder
            }
        }
    }

    // Warte, bis Bilder geladen sind
    if (userImages == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Bilder werden geladen...")
        }
        return
    }

    // Bilder für das Spiel auswählen
    val selectedImages = selectImagesForGrid(
        userImages = userImages ?: emptyList(),
        localImages = localImages,
        requiredPairs = requiredPairs
    )
    val cards = remember {
        mutableStateListOf(*selectedImages.flatMap { listOf(it, it) }.shuffled().toTypedArray())
    }

    var flippedCards by remember { mutableStateOf(listOf<Int>()) }
    var matchedPairs by remember { mutableStateOf(0) }

    LaunchedEffect(flippedCards) {
        if (flippedCards.size == 2) {
            delay(500)
            if (cards[flippedCards[0]] == cards[flippedCards[1]]) {
                matchedPairs++
            } else {
                flippedCards = listOf()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(cards.size) { index ->
                val card = cards[index]
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(8.dp)
                        .clickable {
                            if (flippedCards.size < 2 && index !in flippedCards) {
                                flippedCards = flippedCards + index
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = card),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Text(
            text = "Gefundene Paare: $matchedPairs / $requiredPairs",
            modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
        )
    }
}

// Simulierte Funktion für Backend-Daten
suspend fun fetchUserImagesFromBackend(): List<Int> {
    delay(1000) // Simuliere Netzwerkverzögerung
    return listOf(
        R.drawable.image1, R.drawable.image2, R.drawable.image3/*, R.drawable.image4,
        R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8,
        R.drawable.image9, R.drawable.image10, R.drawable.image11*/
    )
}
