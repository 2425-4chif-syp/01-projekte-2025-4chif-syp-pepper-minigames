package com.example.memorygame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memorygame.data.AppDatabase

@Composable
fun HighScoresScreen(currentPlayerId: Long) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val playerScoreDao = db.playerScoreDao()

    val scores = playerScoreDao.getAllScores().collectAsState(initial = emptyList()).value

    val backgroundColor = Color.White
    val cardColor = Color(0xFF112D4E)
    val bestScoreColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(backgroundColor)
    ) {
        if (scores.isNotEmpty()) {
            val bestScore = scores.maxByOrNull { it.score }

            Text(
                text = "🏆 Dein Höchster Rekord",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF112D4E),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                elevation = 10.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = cardColor
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Text("Name", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
                        Text("Grid", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
                        Text("Zeit", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
                        Text("Punkte", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
                    }

                    bestScore?.let {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bestScoreColor)
                                .padding(8.dp)
                        ) {
                            Text("${it.vorName} ${it.nachName}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.Black)
                            Text("${it.gridRows}x${it.gridColumns}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.Black)
                            Text("${it.elapsedTime}s", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.Black)
                            Text("${it.score}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.Black)
                        }
                    } ?: Text("Noch kein bester Score vorhanden.", color = Color.White)
                }
            }
        }

        Text(
            text = "Deine zuletzt geschafften Punkte",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF112D4E),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn {
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text("Name", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Color(0xFF112D4E))
                    Text("Grid", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Color(0xFF112D4E))
                    Text("Zeit", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Color(0xFF112D4E))
                    Text("Punkte", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Color(0xFF112D4E))
                }
            }

            items(scores.sortedByDescending { it.date }) { score ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = cardColor
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text("${score.vorName} ${score.nachName}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.White)
                        Text("${score.gridRows}x${score.gridColumns}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.White)
                        Text("${score.elapsedTime}s", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.White)
                        Text("${score.score}", modifier = Modifier.weight(1f), fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
