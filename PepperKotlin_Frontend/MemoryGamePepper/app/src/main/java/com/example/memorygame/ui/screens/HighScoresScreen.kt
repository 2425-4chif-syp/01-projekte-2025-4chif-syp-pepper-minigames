package com.example.memorygame.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.memorygame.data.AppDatabase
import com.example.memorygame.data.PlayerScoreDao

@Composable
fun HighScoresScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val playerScoreDao = db.playerScoreDao()

    val scores = playerScoreDao.getAllScores().collectAsState(initial = emptyList()).value

    LazyColumn {
        items(scores) { score ->
            Text(
                "${score.vorName} ${score.nachName}: ${score.score} Punkte (${score.elapsedTime}s) " +
                        "auf ${score.gridRows}x${score.gridColumns}-Grid"
            )
        }
    }

}

