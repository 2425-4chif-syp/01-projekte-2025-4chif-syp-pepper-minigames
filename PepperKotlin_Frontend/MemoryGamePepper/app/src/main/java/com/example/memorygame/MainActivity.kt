package com.example.memorygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.memorygame.ui.theme.MemoryGameTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemoryGameTheme {
                MemoryGame()
            }
        }
    }
}

@Composable
fun MemoryGame(modifier: Modifier = Modifier) {
    val cards = remember { mutableStateListOf(*createMemoryDeck().toTypedArray()) }
    var flippedCards by remember { mutableStateOf(listOf<MemoryCard>()) }
    var attempts by remember { mutableStateOf(0) }
    var pairsFound by remember { mutableStateOf(0) }
    val context = LocalContext.current

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
        // Hintergrundbild
        Image(
            painter = painterResource(id = R.drawable.jungle), // Hintergrundbild
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Spielfeld
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(cards.size) { index ->
                MemoryCardView(
                    card = cards[index],
                    onClick = {
                        if (flippedCards.size < 2 && !cards[index].isFlippedState && !cards[index].isMatchedState) {
                            cards[index].isFlippedState = true
                            flippedCards = flippedCards + cards[index]
                        }
                    }
                )
            }
        }

        // Spielinformationen
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Versuche: $attempts",
                style = MaterialTheme.typography.h6,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gefundene Paare: $pairsFound / ${cards.size / 2}",
                style = MaterialTheme.typography.h6,
                color = Color.Black
            )
        }
    }
}

@Composable
fun MemoryCardView(card: MemoryCard, onClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Berechne die Kartengröße, sodass alle Karten auf den Bildschirm passen
    val cardWidth = screenWidth / 5
    val cardHeight = screenHeight / 5

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(cardWidth)
            .height(cardHeight)
            .clickable(enabled = !card.isMatchedState && !card.isFlippedState) {
                onClick()
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
