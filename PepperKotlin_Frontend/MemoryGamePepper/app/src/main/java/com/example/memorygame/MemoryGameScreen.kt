package com.example.memorygame

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.memorygame.logic.ScoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.memorygame.ui.dialogs.WinDialog
import com.example.memorygame.logic.restartGame

@Composable
fun MemoryGameScreen(navController: NavHostController, rows: Int, columns: Int) {
    val scoreManager = remember { ScoreManager(rows, columns) }
    val gameLogic = remember { GameLogic(scoreManager) }

    val selectedImages = cardImages.shuffled().take((rows * columns) / 2)
    val cards = remember {
        mutableStateListOf(*selectedImages.flatMap { listOf(MemoryCard(it.hashCode(), it), MemoryCard(it.hashCode(), it)) }.shuffled().toTypedArray())
    }

    val flippedCards by remember { derivedStateOf { gameLogic.flippedCards } }
    val matchedCards by remember { derivedStateOf { gameLogic.matchedCards } }
    val isGameOver by remember { derivedStateOf { gameLogic.isGameOver } }
    val coroutineScope = rememberCoroutineScope()

    var gameStartTime by remember { mutableStateOf(0L) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val gridAvailableHeight = screenHeight - 70.dp
    val cardWidth = screenWidth / columns
    val cardHeight = gridAvailableHeight / rows

    // Timer starten
    LaunchedEffect(isGameOver) {
        if (!gameLogic.isGameOver) {
            elapsedSeconds = 0
            gameStartTime = System.currentTimeMillis()
            while (!gameLogic.isGameOver) {
                delay(1000L)
                elapsedSeconds++
            }
            val totalGameTimeSeconds = ((System.currentTimeMillis() - gameStartTime) / 1000).toInt()
            scoreManager.applyTimeBonus(totalGameTimeSeconds, rows, columns)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.jungle),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (isGameOver) {
            WinDialog(
                onRestart = {
                    restartGame(cards, matchedCards, flippedCards, rows, columns, scoreManager)
                    gameLogic.isGameOver = false
                },
                onGoToMainMenu = {
                    navController.navigate("main_menu")
                },
                elapsedSeconds = elapsedSeconds,
                scoreManager = scoreManager
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
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
                                coroutineScope.launch {
                                    gameLogic.flipCard(index, cards)
                                }
                            }
                        }
                        .border(borderWidth, borderColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (isFlipped) {
                        Image(
                            painter = painterResource(id = card.image),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.question_mark),
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
            val formattedTime = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)
            Text(text = "Deine Zeit: $formattedTime", color = Color.Black, fontSize = 18.sp)
            Text(
                text = "Punkte: ${scoreManager.currentScore}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}
