package com.example.memorygame.logic

import com.example.memorygame.MemoryCard
import com.example.memorygame.cardImages

fun restartGame(
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
}