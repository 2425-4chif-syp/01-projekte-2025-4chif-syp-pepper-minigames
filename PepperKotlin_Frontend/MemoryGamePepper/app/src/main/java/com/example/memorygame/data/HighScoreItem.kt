package com.example.memorygame.data

data class HighScoreItem(
    val personId: Long,
    val firstName: String,
    val lastName: String,
    val gridRows: Int,
    val gridColumns: Int,
    val score: Int,
    val elapsedTime: Int,
    val date: Long
)
