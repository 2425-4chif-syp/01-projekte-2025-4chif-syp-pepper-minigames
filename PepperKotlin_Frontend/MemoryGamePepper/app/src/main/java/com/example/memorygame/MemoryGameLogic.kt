package com.example.memorygame

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
<<<<<<< HEAD
<<<<<<< HEAD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
=======
import com.example.memorygame.logic.ScoreManager
>>>>>>> bfc662559ef0baa91b58eff2524a9c99fa6378f2
=======
=======
import com.example.memorygame.logic.ScoreManager
>>>>>>> main
>>>>>>> main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MemoryCard(val id: Int, val image: Int, var isFlipped: Boolean = false, var isMatched: Boolean = false)

val cardImages = listOf(
    R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4,
    R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8
)

fun createMemoryDeck(): List<MemoryCard> {
    val cards = cardImages.flatMap { listOf(MemoryCard(it.hashCode(), it), MemoryCard(it.hashCode(), it)) }
    return cards.shuffled()
}

<<<<<<< HEAD
<<<<<<< HEAD
class GameLogic(private val textToSpeech: TextToSpeech?, private val coroutineScope: CoroutineScope) {
=======
class GameLogic(private val scoreManager: ScoreManager) {
>>>>>>> bfc662559ef0baa91b58eff2524a9c99fa6378f2
=======
class GameLogic {
=======
class GameLogic(private val scoreManager: ScoreManager) {
>>>>>>> main
>>>>>>> main
    var flippedCards by mutableStateOf(mutableListOf<Int>())
    var matchedCards by mutableStateOf(mutableSetOf<Int>())
    var isGameOver by mutableStateOf(false)

    suspend fun flipCard(cardIndex: Int, allCards: MutableList<MemoryCard>) {
        if (cardIndex !in flippedCards && flippedCards.size < 2 && cardIndex !in matchedCards) {
            flippedCards = mutableListOf(*flippedCards.toTypedArray(), cardIndex)
            allCards[cardIndex].isFlipped = true

            if (flippedCards.size == 2) {
                delay(300)
                checkForMatch(allCards)
<<<<<<< HEAD
            }
        }
    }

    private fun checkForMatch(allCards: MutableList<MemoryCard>) {
        if (flippedCards.size == 2) {
            val firstCardIndex = flippedCards[0]
            val secondCardIndex = flippedCards[1]
            val firstCard = allCards[firstCardIndex]
            val secondCard = allCards[secondCardIndex]

            if (firstCard.image == secondCard.image) {
                matchedCards.add(firstCardIndex)
                matchedCards.add(secondCardIndex)
<<<<<<< HEAD
<<<<<<< HEAD

                // 🎙️ Pepper spricht nur, wenn das Spiel nicht vorbei ist
                if (!isGameOver) {
                    speakWithPepper(
                        listOf(
                            "Jo, passt!",
                            "Guat g'macht!",
                            "Jo sicher!",
                            "Bumm, des woar guat!",
                            "Freili, weiter so!",
                            "Läuft wia g'schmiert!",
                            "Sauba!",
                            "Volltreffer!",
                            "Stark!"
                        )
                    )
                }

            } else {
                firstCard.isFlipped = false
                secondCard.isFlipped = false

                // 🎙️ Falsches Paar → Pepper reagiert
                if (!isGameOver) {
                    speakWithPepper(
                        listOf(
                            "Naaa, nix da!",
                            "Probier’s no amoi!",
                            "Dös passt net!",
                            "Knapp vorbei!",
                            "Schade, oba weida!",
                            "Bissl besser aufpassn!",
                            "Schärfer schaun!",
                            "Net aufgebn!",
                            "Kanz knapp!"
                        )
                    )
                }
=======
=======
            } else {
                firstCard.isFlipped = false
                secondCard.isFlipped = false
            }

            flippedCards = mutableListOf()

            if (matchedCards.size == allCards.size) {
                isGameOver = true
=======
>>>>>>> main
            }
        }
    }

<<<<<<< HEAD
    fun restartGame(): MutableList<MemoryCard> {
        flippedCards = mutableListOf()
        matchedCards = mutableSetOf()
        isGameOver = false
        return createMemoryDeck().toMutableList()
=======
    private fun checkForMatch(allCards: MutableList<MemoryCard>) {
        if (flippedCards.size == 2) {
            val firstCardIndex = flippedCards[0]
            val secondCardIndex = flippedCards[1]
            val firstCard = allCards[firstCardIndex]
            val secondCard = allCards[secondCardIndex]

            if (firstCard.image == secondCard.image) {
                matchedCards.add(firstCardIndex)
                matchedCards.add(secondCardIndex)
>>>>>>> main
                scoreManager.onMatchFound()
            } else {
                firstCard.isFlipped = false
                secondCard.isFlipped = false
                scoreManager.onMismatch()
>>>>>>> bfc662559ef0baa91b58eff2524a9c99fa6378f2
            }

            flippedCards = mutableListOf()

            if (matchedCards.size == allCards.size) {
                isGameOver = true
            }
        }
>>>>>>> main
    }
<<<<<<< HEAD

    fun restartGame(): MutableList<MemoryCard> {
        flippedCards = mutableListOf()
        matchedCards = mutableSetOf()
        isGameOver = false
        return createMemoryDeck().toMutableList()
    }

    // 🎙️ Funktion für Peppers Sprachsteuerung
    private fun speakWithPepper(phrases: List<String>) {
        if (textToSpeech != null) {
            val randomText = phrases.random()
            coroutineScope.launch(Dispatchers.IO) {
                textToSpeech.speak(
                    randomText,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            }
        }
    }
}
=======
}
>>>>>>> bfc662559ef0baa91b58eff2524a9c99fa6378f2
