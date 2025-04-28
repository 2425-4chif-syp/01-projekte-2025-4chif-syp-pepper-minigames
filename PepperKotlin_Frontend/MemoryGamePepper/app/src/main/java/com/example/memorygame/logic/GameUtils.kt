package com.example.memorygame.logic

import com.example.memorygame.MemoryCard
import com.example.memorygame.cardImages
import com.example.memorygame.logic.image.ImageData

fun restartGame(
    cards: MutableList<MemoryCard>,
    matchedCards: MutableSet<Int>,
    flippedCards: MutableList<Int>,
    rows: Int,
    columns: Int,
    scoreManager: ScoreManager,
    images: List<ImageData>
) {
    matchedCards.clear()
    flippedCards.clear()

    val neededPairs = (rows * columns) / 2
    val selectedImages = images.shuffled().take(neededPairs)

    cards.clear()
    cards.addAll(selectedImages.flatMap { image ->
        listOf(
            MemoryCard(id = image.hashCode(), image = image),
            MemoryCard(id = image.hashCode(), image = image)
        )
    }.shuffled())

    cards.forEach { it.isFlipped = false }
    scoreManager.resetScore()
}

/*fun restartGame(
    cards: MutableList<MemoryCard>,  // Das Karten-Deck
    matchedCards: MutableSet<Int>,   // Die gematchten Karten
    flippedCards: MutableList<Int>,  // Die aktuell umgedrehten Karten
    rows: Int,
    columns: Int,
    scoreManager: ScoreManager
) {
    matchedCards.clear()  // Entfernt alle gematchten Karten
    flippedCards.clear()  // Entfernt alle umgedrehten Karten

    val selectedImages = cardImages.shuffled().take((rows * columns) / 2)
    cards.clear()  // Löscht das bestehende Karten-Deck
    cards.addAll(selectedImages.flatMap { listOf(MemoryCard(it.hashCode(), it), MemoryCard(it.hashCode(), it)) }.shuffled())

    // Alle Karten zurückdrehen
    cards.forEach { it.isFlipped = false }
    scoreManager.resetScore()
}*/