package com.example.memorygame.data

data class ScoreRequest(
    val personId: Long,
    val vorName: String,
    val nachName: String,
    val gridRows: Int,
    val gridColumns: Int,
    val score: Int,
    val elapsedTime: Int
)
