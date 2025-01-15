package com.example.memorygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memorygame.ui.theme.MemoryGameTheme
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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

fun setMediaVolume(context: Context) {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.setStreamVolume(
        AudioManager.STREAM_MUSIC,
        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
        0
    )
}

fun playSound(context: Context, soundResId: Int) {
    val mediaPlayer = MediaPlayer.create(context, soundResId)
    mediaPlayer.setVolume(1.0f, 1.0f)
    mediaPlayer.start()
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
}

@Composable
fun MemoryGame(modifier: Modifier = Modifier) {
    val cards = remember { mutableStateListOf(*createMemoryDeck().toTypedArray()) }
    var flippedCards by remember { mutableStateOf(listOf<MemoryCard>()) }
    var attempts by remember { mutableStateOf(0) }
    var pairsFound by remember { mutableStateOf(0) }
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    LaunchedEffect(flippedCards) {
        if (flippedCards.size == 2) {
            attempts += 1
            kotlinx.coroutines.delay(300)

            if (flippedCards[0].id != flippedCards[1].id) {
                playSound(context, R.raw.fail)
                kotlinx.coroutines.delay(1000)
                flippedCards.forEach { it.isFlippedState = false }
            } else {
                playSound(context, R.raw.success)
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
            painter = painterResource(id = R.drawable.jungle),
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
                        if (flippedCards.size < 2 && !cards[index].isFlippedState && !cards[index].isMatchedState && !isPaused) {
                            cards[index].isFlippedState = true
                            flippedCards = flippedCards + cards[index]
                        }
                    }
                )
            }
        }

        // Informationen über Versuche und Paare
        Column(
            modifier = Modifier
                .padding(3.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .wrapContentSize()
                    .border(1.dp, Color.Black)
            ) {
                Column(
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

        // Menü-Button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Optionen"
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(onClick = {
                    isPaused = !isPaused
                    showMenu = false
                }) {
                    Text(text = if (isPaused) "Spiel fortsetzen" else "Spiel pausieren")
                }
                DropdownMenuItem(onClick = {
                    attempts = 0
                    pairsFound = 0
                    flippedCards = listOf()
                    cards.clear()
                    cards.addAll(createMemoryDeck())
                    showMenu = false
                }) {
                    Text(text = "Spiel neustarten")
                }
            }
        }
    }
}



@Composable
fun MemoryCardView(card: MemoryCard, onClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val cardHeight = screenHeight / 5 // Dynamische Höhe basierend auf Bildschirmgröße

    Card(
        modifier = Modifier
            .padding(8.dp)
            .height(cardHeight)
            .fillMaxWidth()
            .clickable(enabled = !card.isMatchedState && !card.isFlippedState) {
                onClick()
            },
        backgroundColor = Color.Transparent,
        elevation = 8.dp
    ) {
        if (card.isFlippedState || card.isMatchedState) {
            // Angepasstes Bild, das sich der Kartengröße anpasst
            Image(
                painter = painterResource(id = card.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(), // Füllt die Karte vollständig aus
                contentScale = ContentScale.Fit // Bild passt sich an die Karte an
            )
        } else {
            // Rückseite der Karte mit Anpassung
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.question_mark),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Rückseitenbild füllt die Karte vollständig aus
                )
            }
        }
    }
}
