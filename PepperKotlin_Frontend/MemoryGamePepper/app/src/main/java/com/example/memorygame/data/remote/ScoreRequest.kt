package com.example.memorygame.data

data class ScoreRequest(
    val comment: String,
    val dateTime: String,
    val elapsedTime: Int,
    val score: Int,
    val person: Person, // ❗ statt PersonRef
    val game: Game      // ❗ statt GameRef
)

data class PersonRef(val id: Long)
data class GameRef(val id: Long)
