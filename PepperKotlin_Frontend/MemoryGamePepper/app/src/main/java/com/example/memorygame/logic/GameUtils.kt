package com.example.memorygame.logic

import com.example.memorygame.MemoryCard
import com.example.memorygame.cardImages

// Allgemeine Restart-Funktion
fun restartGame(
    cards: MutableList<MemoryCard>,  // Das Karten-Deck
    matchedCards: MutableSet<Int>,   // Die gematchten Karten
    flippedCards: MutableList<Int>,  // Die aktuell umgedrehten Karten
    rows: Int,                       // Anzahl der Zeilen im Grid
    columns: Int                      // Anzahl der Spalten im Grid
) {
    matchedCards.clear()  // Entfernt alle gematchten Karten
    flippedCards.clear()  // Entfernt alle umgedrehten Karten

    // Karten neu generieren
    val selectedImages = cardImages.shuffled().take((rows * columns) / 2)
    cards.clear()  // Löscht das bestehende Karten-Deck
    cards.addAll(selectedImages.flatMap { listOf(MemoryCard(it.hashCode(), it), MemoryCard(it.hashCode(), it)) }.shuffled())

    // Alle Karten zurückdrehen
    cards.forEach { it.isFlipped = false }
}