package com.example.memorygame.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerScoreDao {
    @Insert
    suspend fun insertScore(score: PlayerScore)

    @Query("SELECT * FROM player_scores ORDER BY score DESC")
    fun getAllScores(): Flow<List<PlayerScore>>

    @Query("SELECT * FROM player_scores WHERE personId = :personId ORDER BY score DESC")
    fun getScoresByPerson(personId: Long): Flow<List<PlayerScore>>
}
