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
    val gridItems = mutableStateOf(List(48) { Random.nextInt(0, 5) }) // 8x6 Grid mit zufälligen Bildern
    val thiefPosition = mutableStateOf(Random.nextInt(0, 48)) // Zufällige Position des Diebes
    val gameWon = mutableStateOf(false) // Überprüfung, ob das Spiel gewonnen wurde
    val elapsedTime = mutableStateOf(0L) // Zeit, die seit Spielbeginn vergangen ist
    private var mediaPlayer: MediaPlayer? = null // MediaPlayer für Sounds
    private var isTimerRunning = mutableStateOf(true) // Kontrolliert den Timer

    // Hier definieren wir die images- und sounds-Listen
    val images = listOf(
        R.drawable.water,
        R.drawable.church,
        R.drawable.sheep,
        R.drawable.witch,
        R.drawable.bird
    )

    val sounds = listOf(
        R.raw.water_sound,
        R.raw.church_bells,
        R.raw.sheep_bleat,
        R.raw.witch_laugh,
        R.raw.bird_chirp
    )

    init {
        startTimer()
    }

    // Startet den Timer
    fun startTimer() {
        viewModelScope.launch(Dispatchers.Main) {  // Verwende den Haupt-Thread (Main Dispatcher)
            while (!gameWon.value) {
                delay(1000) // Eine Sekunde warten
                elapsedTime.value += 1000 // Zeit um 1 Sekunde erhöhen
            }
        }
    }

    // Stoppt den Timer
    fun stopTimer() {
        isTimerRunning.value = false
    }

    // Setzt das Spiel zurück
    fun resetGame() {
        gridItems.value = List(48) { Random.nextInt(0, images.size) } // Stelle sicher, dass der Index im gültigen Bereich bleibt (0 bis 4)
        thiefPosition.value = (0 until 48).random() // Neue zufällige Position des Diebes
        gameWon.value = false // Spiel nicht gewonnen
        elapsedTime.value = 0L // Zeit zurücksetzen
        isTimerRunning.value = true // Timer starten
        startTimer() // Timer neu starten
    }


    // Stoppt das Spiel
    fun stopGame() {
        stopTimer() // Timer stoppen
        mediaPlayer?.release() // MediaPlayer stoppen
        mediaPlayer = null
        Log.d("GameViewModel", "Game beendet.")
    }

    // Setzt das Spiel auf gewonnen
    fun setGameWon(won: Boolean) {
        gameWon.value = won
    }

    // Logik für die Bewegung des Diebes
    fun moveThief(): Int {
        val possibleMoves = mutableListOf<Int>()
        if (thiefPosition.value % 8 != 0) possibleMoves.add(thiefPosition.value - 1) // Links
        if (thiefPosition.value % 8 != 7) possibleMoves.add(thiefPosition.value + 1) // Rechts
        if (thiefPosition.value >= 8) possibleMoves.add(thiefPosition.value - 8) // Oben
        if (thiefPosition.value < 40) possibleMoves.add(thiefPosition.value + 8) // Unten
        thiefPosition.value = possibleMoves.random() // Zufällige Bewegung des Diebes

        // Berechne die Zeile und Spalte basierend auf der Position des Diebes (1-basiert)
        val row = thiefPosition.value / 8 + 1 // Zeile 1-basiert
        val column = thiefPosition.value % 8 + 1 // Spalte 1-basiert

        // Logge die Position des Diebes
        Log.d("GameViewModel", "Der Dieb befindet sich in Grid [$row][$column]")

        // Sofort den Sound des neuen Grid abspielen
        viewModelScope.launch {
            playSound(thiefPosition.value) // Spiele den entsprechenden Sound ab
        }

        return thiefPosition.value
    }

    // Methode zum Abspielen von Sounds
    suspend fun playSound(thiefPosition: Int) {
        try {
            // Hole den Bild-Index an der Position des Diebes
            val imageIndex = gridItems.value[thiefPosition]
            // Hole den entsprechenden Sound basierend auf dem Bild-Index
            val soundResId = sounds[imageIndex]

            // Vorherigen Sound stoppen, falls er noch läuft
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }

            // Initialisiere den MediaPlayer mit dem entsprechenden Sound
            mediaPlayer = MediaPlayer.create(getApplication(), soundResId)
            mediaPlayer?.setOnCompletionListener {
                mediaPlayer?.release() // Release nach Beendigung
                mediaPlayer = null
            }
            mediaPlayer?.start() // Starte den neuen Sound
        } catch (e: Exception) {
            Log.e("GameViewModel", "Error playing sound: ${e.message}")
        }
    }
}
