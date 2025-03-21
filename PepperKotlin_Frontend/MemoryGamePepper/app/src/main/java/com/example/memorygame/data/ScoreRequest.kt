package com.example.memorygame.data

data class ScoreRequest(
    val personId: Long,
    val firstName: String,
    val lastName: String,
    val grid: String,
    val score: Int,
    val elapsedTime: Int,
    val date: Long = System.currentTimeMillis()
)
