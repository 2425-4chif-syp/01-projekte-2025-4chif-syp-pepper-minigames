package com.example.memorygame.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_scores")
data class PlayerScore(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val personId: Long,
    val vorName: String,
    val nachName: String,
    val gridRows: Int,
    val gridColumns: Int,
    val score: Int,
    val elapsedTime: Int,
    val date: Long = System.currentTimeMillis()
)

