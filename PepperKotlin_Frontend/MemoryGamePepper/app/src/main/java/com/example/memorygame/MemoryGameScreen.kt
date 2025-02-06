package com.example.memorygame

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.memorygame.logic.selectImagesForGrid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoryGameScreen(rows: Int, columns: Int) {
    val userImages = listOf(
        R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4,
        R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8/*,
        R.drawable.image9, R.drawable.image10, R.drawable.image11*/
    )

    val localImages = listOf(
        R.drawable.local1, R.drawable.local2, R.drawable.local3, R.drawable.local4,
        R.drawable.local5, R.drawable.local6, R.drawable.local7, R.drawable.local8
    )

    val requiredPairs = (rows * columns) / 2
    val selectedImages = if (userImages.size >= requiredPairs) {
        userImages.shuffled().take(requiredPairs)
    } else {
        userImages + localImages.shuffled().take(requiredPairs - userImages.size)
    }

    val cards = remember {
        mutableStateListOf(*selectedImages.flatMap { listOf(it, it) }.shuffled().toTypedArray())
    }
    var flippedCards by remember { mutableStateOf(listOf<Int>()) }
    var matchedCards by remember { mutableStateOf(mutableSetOf<Int>()) }

    LaunchedEffect(flippedCards) {
        if (flippedCards.size == 2) {
            delay(1000)
            val firstCard = flippedCards[0]
            val secondCard = flippedCards[1]
            if (cards[firstCard] == cards[secondCard]) {
                matchedCards.addAll(flippedCards)
            }
            flippedCards = listOf()
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val cardWidth = screenWidth / columns
    val cardHeight = screenHeight / rows

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.jungle),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            items(cards.size) { index ->
                val isFlipped = index in flippedCards || index in matchedCards
                Box(
                    modifier = Modifier
                        .width(cardWidth)
                        .height(cardHeight)
                        .padding(4.dp)
                        .background(Color.Gray)
                        .clickable(enabled = !isFlipped) {
                            if (flippedCards.size < 2 && index !in matchedCards) {
                                flippedCards = flippedCards + index
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isFlipped) {
                        Image(
                            painter = painterResource(id = cards[index]),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "?", color = Color.White)
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

// Simulierte Funktion für Backend-Daten
suspend fun fetchUserImagesFromBackend(): List<Int> {
    delay(1000) // Simuliere Netzwerkverzögerung
    return listOf(
        R.drawable.image1, R.drawable.image2/*, R.drawable.image3, R.drawable.image4,
        R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8,
        R.drawable.image9, R.drawable.image10, R.drawable.image11*/
    )
}
