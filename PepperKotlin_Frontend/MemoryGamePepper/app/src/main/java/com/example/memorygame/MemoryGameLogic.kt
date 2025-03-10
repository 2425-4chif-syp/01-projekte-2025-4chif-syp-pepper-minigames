package com.example.memorygame

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

class GameLogic(private val textToSpeech: TextToSpeech?, private val coroutineScope: CoroutineScope) {
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
            }

            flippedCards = mutableListOf()

            if (matchedCards.size == allCards.size) {
                isGameOver = true
            }
        }
    }

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
