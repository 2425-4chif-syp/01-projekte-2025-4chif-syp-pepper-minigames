package com.example.memorygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun MemoryGameScreen(rows: Int, columns: Int, modifier: Modifier = Modifier) {
    val cards = remember { mutableStateListOf(*createMemoryDeck().toTypedArray()) }
    var flippedCards by remember { mutableStateOf(listOf<MemoryCard>()) }
    var attempts by remember { mutableStateOf(0) }
    var pairsFound by remember { mutableStateOf(0) }

    LaunchedEffect(flippedCards) {
        if (flippedCards.size == 2) {
            attempts += 1
            delay(300)

            if (flippedCards[0].id != flippedCards[1].id) {
                delay(300)
                flippedCards.forEach { it.isFlippedState = false }
            } else {
                flippedCards[0].isMatchedState = true
                flippedCards[1].isMatchedState = true
                pairsFound += 1
            }
            flippedCards = listOf()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(cards.size) { index ->
                val card = cards[index]
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(100.dp) // Beispielgröße für Karten
                        .clickable(enabled = !card.isMatchedState && !card.isFlippedState) {
                            if (flippedCards.size < 2) {
                                card.isFlippedState = true
                                flippedCards = flippedCards + card
                            }
                        },
                    backgroundColor = Color.Transparent,
                    elevation = 8.dp
                ) {
                    if (card.isFlippedState || card.isMatchedState) {
                        Image(
                            painter = painterResource(id = card.image),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.question_mark),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}
