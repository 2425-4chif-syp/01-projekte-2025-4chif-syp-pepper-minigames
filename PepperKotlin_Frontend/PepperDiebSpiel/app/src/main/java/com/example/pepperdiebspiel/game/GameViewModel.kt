package com.example.pepperdiebspiel.game

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pepperdiebspiel.R
import kotlinx.coroutines.*
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val gridItems = mutableStateOf(emptyList<Int>())
    val thiefPosition = mutableStateOf(0)
    val gameWon = mutableStateOf(false)
    val elapsedTime = mutableStateOf(0L)
    val gameOver = mutableStateOf(false)
    val timeLimit = mutableStateOf(60_000L)

    val images = mutableStateOf<List<Int>>(emptyList())
    val sounds = mutableStateOf<List<Int>>(emptyList())

    private var mediaPlayer: MediaPlayer? = null
    private var isTimerRunning = mutableStateOf(true)
    private var gridSize = 48

    fun setDifficultyAndTheme(difficulty: String, theme: String) {
        gridSize = when (difficulty) {
            "easy" -> 16
            "medium" -> 30
            "hard" -> 48
            else -> 30
        }

        timeLimit.value = when (difficulty) {
            "easy" -> 30_000L
            "medium" -> 30_000L
            "hard" -> 45_000L
            else -> 60_000L
        }

        when (theme) {
            "classic" -> {
                images.value = listOf(
                    R.drawable.water, R.drawable.church, R.drawable.sheep,
                    R.drawable.witch, R.drawable.bird
                )
                sounds.value = listOf(
                    R.raw.water_sound, R.raw.church_bells, R.raw.sheep_bleat,
                    R.raw.witch_laugh, R.raw.bird_chirp
                )
            }

            "scary" -> {
                images.value = listOf(
                    R.drawable.wolf, R.drawable.vampir, R.drawable.monster,
                    R.drawable.zombie, R.drawable.skeleton
                )
                sounds.value = listOf(
                    R.raw.wolf_howl, R.raw.vampire, R.raw.monster,
                    R.raw.zombie, R.raw.skeleton
                )
            }

            "night" -> {
                images.value = listOf(
                    R.drawable.owl, R.drawable.wolf, R.drawable.bird,
                    R.drawable.sheep, R.drawable.water
                )
                sounds.value = listOf(
                    R.raw.owl, R.raw.wolf_howl, R.raw.bird_chirp,
                    R.raw.sheep_bleat, R.raw.water_sound
                )
            }

            "chaos" -> {
                images.value = listOf(
                    R.drawable.cow, R.drawable.skeleton, R.drawable.monster,
                    R.drawable.witch, R.drawable.alarm
                )
                sounds.value = listOf(
                    R.raw.cow, R.raw.skeleton, R.raw.monster,
                    R.raw.witch_laugh, R.raw.alarm
                )
            }

            else -> {
                images.value = emptyList()
                sounds.value = emptyList()
            }
        }

        if (images.value.isEmpty() || sounds.value.isEmpty()) {
            Log.e("GameViewModel", "Fehler: Kein Thema gesetzt oder leere Bild-/Soundliste!")
            return
        }

        gridItems.value = List(gridSize) { Random.nextInt(images.value.size) }
        thiefPosition.value = Random.nextInt(gridSize)
        gameWon.value = false
        gameOver.value = false
        elapsedTime.value = 0L
        isTimerRunning.value = true

        Log.d("GameViewModel", "Spiel gestartet mit $gridSize Feldern, Thema: $theme")
        startTimer()
    }


    fun resetGame(difficulty: String, theme: String) {
        stopTimer()
        setDifficultyAndTheme(difficulty, theme)
    }

    fun stopGame() {
        stopTimer()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("GameViewModel", "Game beendet.")
    }

    fun stopTimer() {
        isTimerRunning.value = false
    }

    fun setGameWon(won: Boolean) {
        gameWon.value = won
    }

    fun startTimer() {
        viewModelScope.launch(Dispatchers.Main) {
            while (!gameWon.value && isTimerRunning.value) {
                delay(1000)
                elapsedTime.value += 1000

                if (elapsedTime.value >= timeLimit.value) {
                    gameOver.value = true
                    isTimerRunning.value = false
                    break
                }
            }
        }
    }



    fun moveThief(): Int {
        if (images.value.isEmpty() || gridItems.value.isEmpty()) return thiefPosition.value

        val cols = when (gridSize) {
            30 -> 5
            48 -> 6
            80 -> 8
            else -> 6
        }

        val moves = mutableListOf<Int>()
        val pos = thiefPosition.value

        if (pos % cols != 0) moves.add(pos - 1)
        if (pos % cols != cols - 1) moves.add(pos + 1)
        if (pos >= cols) moves.add(pos - cols)
        if (pos < gridSize - cols) moves.add(pos + cols)

        if (moves.isNotEmpty()) {
            thiefPosition.value = moves.random()
        }

        Log.d("GameViewModel", "Dieb bewegt zu Position ${thiefPosition.value}")

        viewModelScope.launch {
            playSound(thiefPosition.value)
        }

        return thiefPosition.value
    }


    suspend fun playSound(thiefPosition: Int) {
        try {
            val imageIndex = gridItems.value[thiefPosition]
            val soundResId = sounds.value.getOrNull(imageIndex) ?: return

            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }

            mediaPlayer = MediaPlayer.create(getApplication(), soundResId)
            mediaPlayer?.setOnCompletionListener {
                mediaPlayer?.release()
                mediaPlayer = null
            }
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e("GameViewModel", "Fehler beim Abspielen: ${e.message}")
        }
    }
}
