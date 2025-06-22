package com.example.pepperdiebspiel

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pepperdiebspiel.game.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameGrid(navController: NavController, difficulty: String, theme: String) {
    val gameViewModel: GameViewModel = viewModel()

    LaunchedEffect(Unit) {
        gameViewModel.setDifficultyAndTheme(difficulty, theme)
    }

    val gridItems by gameViewModel.gridItems
    val thiefPosition by gameViewModel.thiefPosition
    val gameWon by gameViewModel.gameWon
    val elapsedTime by gameViewModel.elapsedTime
    val images by gameViewModel.images

    if (images.isEmpty() || gridItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val (columns, rows) = when (difficulty) {
        "easy" -> 4 to 4
        "medium" -> 5 to 6
        "hard" -> 6 to 8
        else -> 5 to 6
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val cellSize = with(LocalDensity.current) {
        min(screenWidth / columns, screenHeight / rows) - 4.dp
    }

    if (gameWon) {
        gameViewModel.stopTimer()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1B1B1B), Color(0xFF121212)),
                        radius = 1000f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.thief),
                    contentDescription = "Gefundener Dieb",
                    modifier = Modifier
                        .size(220.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = "Dieb wurde gefunden!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5252)
                )

                Text(
                    text = "Benötigte Zeit: ${(elapsedTime / 1000)} Sekunden",
                    fontSize = 20.sp,
                    color = Color(0xFF64B5F6)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { gameViewModel.resetGame(difficulty, theme) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1E88E5)),
                        modifier = Modifier
                            .width(130.dp)
                            .height(50.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                    ) {
                        Text("Neustart", fontSize = 18.sp, color = Color.White)
                    }

                    Button(
                        onClick = {
                            gameViewModel.stopGame()
                            navController.navigate("difficulty_selection") {
                                popUpTo("game") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F)),
                        modifier = Modifier
                            .width(130.dp)
                            .height(50.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                    ) {
                        Text("Beenden", fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }
    }
    else if (gameViewModel.gameOver.value) {
        LaunchedEffect(Unit) {
            navController.navigate("gameOver") {
                popUpTo("game") { inclusive = true }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFB3E5FC), Color(0xFF81D4FA))
                    )
                )
        ) {
            LazyVerticalGrid(
                cells = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize()
            ) {
                items(gridItems.size) { index ->
                    GridItem(imageResId = images[gridItems[index]], size = cellSize) {
                        if (thiefPosition == index) {
                            gameViewModel.setGameWon(true)
                        }
                    }
                }
            }
        }
    }


    LaunchedEffect(gameWon, gameViewModel.gameOver.value) {
        if (!gameWon && !gameViewModel.gameOver.value) {
            while (isActive) {
                delay(4000)
                gameViewModel.moveThief()
            }
        }
    }

}

@Composable
fun GridItem(imageResId: Int, size: Dp, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .padding(2.dp)
            .border(2.dp, Color.DarkGray)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                )
            )
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentScale = ContentScale.Fit
        )
    }
}
