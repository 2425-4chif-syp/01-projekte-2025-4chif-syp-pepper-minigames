package com.example.pepperdiebspiel

import android.media.MediaPlayer
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameGrid() {
    val images = listOf(
        R.drawable.water,
        R.drawable.church,
        R.drawable.sheep,
        R.drawable.witch,
        R.drawable.bird
    )
    val sounds = listOf(
        R.raw.water_sound,
        R.raw.church_bells,
        R.raw.sheep_bleat,
        R.raw.witch_laugh,
        R.raw.bird_chirp
    )

    val thiefImage = R.drawable.thief

    val context = LocalContext.current

    var gridItems by remember { mutableStateOf(List(48) { images.random() }) } // 8x6 = 48 Elemente
    var thiefPosition by remember { mutableStateOf((0 until 48).random()) }
    var gameWon by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }
    val mediaPlayer = remember { mutableStateOf<MediaPlayer?>(null) }

    var isTimerRunning by remember { mutableStateOf(true) }

    var startTime by remember { mutableStateOf(0L) }
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            startTime = System.currentTimeMillis()
            while (isTimerRunning && isActive) {
                elapsedTime = System.currentTimeMillis() - startTime
                delay(500)
                val row = thiefPosition / 8
                val column = thiefPosition % 8
                Log.d("GameGrid", "Current thief position: [${row + 1}|${column + 1}]")
            }
        }
    }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (!gameWon && isActive) {
                delay(4000)
                if (gameWon) break

                // Bewege den Dieb zu einer neuen Position
                thiefPosition = moveThief(thiefPosition)
                val row = thiefPosition / 8
                val column = thiefPosition % 8
                Log.d("GameGrid", "Thief position updated: [${row + 1}|${column + 1}]")

                withContext(Dispatchers.Main) {
                    try {
                        val soundIndex = images.indexOf(gridItems[thiefPosition])
                        Log.d("GameGrid", "Playing sound index: $soundIndex")

                        if (soundIndex in sounds.indices) {
                            mediaPlayer.value?.release()
                            mediaPlayer.value = MediaPlayer.create(context, sounds[soundIndex])
                            mediaPlayer.value?.start()
                        } else {
                            Log.w("GameGrid", "Invalid sound index: $soundIndex, no sound will be played.")
                        }
                    } catch (e: Exception) {
                        Log.e("GameGrid", "Error playing sound: ${e.message}")
                    }
                }
            }
        }
    }

    // Anzeige, wenn das Spiel gewonnen ist
    if (gameWon) {
        // Stoppe das Audio
        isTimerRunning = false
        Log.d("GameGrid", "Game won! Time elapsed: ${(elapsedTime / 1000)} seconds")
        mediaPlayer.value?.release()
        mediaPlayer.value = null

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFEB3B), Color(0xFFF57C00)),
                        radius = 600f
                    )
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Bild des Diebs anzeigen
                Image(
                    painter = painterResource(id = thiefImage),
                    contentDescription = "Gefundener Dieb",
                    modifier = Modifier
                        .size(300.dp) // Dieb-Bild noch größer gemacht
                        .padding(bottom = 24.dp)
                        .shadow(10.dp, shape = RectangleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Zeit anzeigen
                Text(
                    text = "Zeit benötigt: ${(elapsedTime / 1000)} Sekunden",
                    fontSize = 30.sp,
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.shadow(5.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Meldung für den Dieb anzeigen
                Text(
                    text = "Dieb wurde gefunden!",
                    fontSize = 36.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.shadow(5.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Neustart-Button
                Button(
                    onClick = {
                        // Neustart
                        gameWon = false
                        gridItems = List(48) { images.random() }
                        thiefPosition = (0 until 48).random()
                        elapsedTime = 0L
                        isTimerRunning = true
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1976D2)),
                    modifier = Modifier
                        .padding(8.dp)
                        .border(2.dp, Color.White)
                        .shadow(8.dp, shape = RectangleShape)
                ) {
                    Text("Neustart", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    } else {
        // Grid anzeigen und sicherstellen, dass es vollständig auf den Bildschirm passt
        val configuration = LocalConfiguration.current
        val screenHeightPx = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }
        val screenWidthPx = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }

        // Berechne die Größe der einzelnen Zellen basierend auf der Bildschirmgröße
        val cellHeight = screenHeightPx / 6
        val cellWidth = screenWidthPx / 8
        val cellSize = with(LocalDensity.current) { min(cellHeight, cellWidth).toDp() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp)
                .wrapContentSize(align = Alignment.Center)
        ) {
            LazyVerticalGrid(
                cells = GridCells.Fixed(8),
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center)
                    .padding(4.dp)
            ) {
                items(gridItems.size) { index ->
                    val imageResId = gridItems[index]
                    GridItem(imageResId = imageResId, size = cellSize) {
                        Log.d("GameGrid", "Grid item clicked, image ID: $imageResId")
                        if (thiefPosition == index) {
                            gameWon = true
                        }
                    }
                }
            }
        }
    }
}

fun moveThief(currentPosition: Int): Int {
    val possibleMoves = mutableListOf<Int>()

    if (currentPosition % 8 != 0) possibleMoves.add(currentPosition - 1)
    if (currentPosition % 8 != 7) possibleMoves.add(currentPosition + 1)
    if (currentPosition >= 8) possibleMoves.add(currentPosition - 8)
    if (currentPosition < 40) possibleMoves.add(currentPosition + 8)

    val newPosition = possibleMoves.random()
    val oldRow = currentPosition / 8
    val oldColumn = currentPosition % 8
    val newRow = newPosition / 8
    val newColumn = newPosition % 8
    Log.d("GameGrid", "Thief moved from [${oldRow + 1}|${oldColumn + 1}] to [${newRow + 1}|${newColumn + 1}]")
    return newPosition
}

@Composable
fun GridItem(imageResId: Int, size: Dp, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(2.dp) // Kleineres Padding für mehr Platz auf dem Bildschirm
            .size(size) // Verwende die berechnete Zellengröße
            .border(1.dp, Color.Gray)
            .clickable {
                Log.d("GameGrid", "Grid item clicked, image ID: $imageResId")
                onClick()
            }
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
