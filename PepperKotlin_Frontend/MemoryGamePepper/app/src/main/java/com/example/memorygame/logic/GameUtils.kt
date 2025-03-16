package com.example.memorygame.logic

import com.example.memorygame.MemoryCard
import com.example.memorygame.cardImages

<<<<<<< HEAD
// Allgemeine Restart-Funktion
=======
>>>>>>> main
fun restartGame(
    cards: MutableList<MemoryCard>,  // Das Karten-Deck
    matchedCards: MutableSet<Int>,   // Die gematchten Karten
    flippedCards: MutableList<Int>,  // Die aktuell umgedrehten Karten
<<<<<<< HEAD
    rows: Int,                       // Anzahl der Zeilen im Grid
    columns: Int                      // Anzahl der Spalten im Grid
=======
    rows: Int,
    columns: Int,
    scoreManager: ScoreManager
>>>>>>> main
) {
    matchedCards.clear()  // Entfernt alle gematchten Karten
    flippedCards.clear()  // Entfernt alle umgedrehten Karten

<<<<<<< HEAD
    // Karten neu generieren
=======
>>>>>>> main
    val selectedImages = cardImages.shuffled().take((rows * columns) / 2)
    cards.clear()  // Löscht das bestehende Karten-Deck
    cards.addAll(selectedImages.flatMap { listOf(MemoryCard(it.hashCode(), it), MemoryCard(it.hashCode(), it)) }.shuffled())

    // Alle Karten zurückdrehen
    cards.forEach { it.isFlipped = false }
<<<<<<< HEAD
=======
    scoreManager.resetScore()
>>>>>>> main
}