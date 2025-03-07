package com.example.memorygame

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.memorygame.logic.restartGame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.memorygame.ui.dialogs.WinDialog

@Composable
fun MemoryGameScreen(navController: NavHostController, rows: Int, columns: Int) {
    // Verwenden der in MemoryGameLogic.kt definierten Liste von Bildreferenzen
    val selectedImages = cardImages.shuffled().take((rows * columns) / 2)

    val cards = remember {
        mutableStateListOf(*selectedImages.flatMap { listOf(MemoryCard(it.hashCode(), it), MemoryCard(it.hashCode(), it)) }.shuffled().toTypedArray())
    }

    var flippedCards by remember { mutableStateOf(mutableListOf<Int>()) }
    var matchedCards by remember { mutableStateOf(mutableSetOf<Int>()) }

    LaunchedEffect(flippedCards) {
        if (flippedCards.size == 2) {
            delay(300) // Warte, um die Auswahl zu zeigen

            val firstCardIndex = flippedCards[0]
            val secondCardIndex = flippedCards[1]
            val firstCard = cards[firstCardIndex]
            val secondCard = cards[secondCardIndex]

            flippedCards = mutableListOf()


            if (firstCard.image == secondCard.image) {
                matchedCards.add(firstCardIndex)
                matchedCards.add(secondCardIndex)
            } else {
                cards[firstCardIndex].isFlipped = false
                cards[secondCardIndex].isFlipped = false
            }
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val gridAvailableHeight = screenHeight - 70.dp
    val cardWidth = screenWidth / columns
    val cardHeight = gridAvailableHeight / rows

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.jungle),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val totalPairs = cards.size / 2 // Berechne totalPairs basierend auf den aktuellen Karten

        var isGameOver by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = matchedCards.size) {
            if (matchedCards.size / 2 == totalPairs) {
                isGameOver = true
            }
        }

        if (isGameOver) {
            WinDialog(
                onRestart = {
                    isGameOver = false
                    restartGame(cards, matchedCards, flippedCards, rows, columns)
                },
                onGoToMainMenu = {
                    navController.navigate("main_menu")
                }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),  // Grid mit der Anzahl der Spalten
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(cards.size) { index ->
                val card = cards[index]
                val isFlipped = card.isFlipped || index in matchedCards
                val borderColor = if (index in flippedCards) Color.Green else Color.Transparent
                val borderWidth = if (index in flippedCards) 3.dp else 2.dp

                Box(
                    modifier = Modifier
                        .width(cardWidth)
                        .height(cardHeight)
                        .padding(4.dp)
                        .background(Color.Gray)
                        .clickable(enabled = !isFlipped) {
                            if (flippedCards.size < 2 && index !in matchedCards) {
                                // Hinzufügen des Index zur flippedCards-Liste
                                flippedCards = mutableListOf(*flippedCards.toTypedArray(), index) // Zustand wird aktualisiert
                                card.isFlipped = true // Karte umdrehen
                            }
                        }
                        .border(borderWidth, borderColor), // Rand der Karte anpassen
                    contentAlignment = Alignment.Center
                ) {
                    if (isFlipped) {
                        Image(
                            painter = painterResource(id = card.image),  // Bild der Karte anzeigen
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.question_mark), // Fragezeichen anzeigen
                                contentDescription = "Question Mark",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Gefundene Paare: ${matchedCards.size / 2}", color = Color.White)
        }
    }
}
