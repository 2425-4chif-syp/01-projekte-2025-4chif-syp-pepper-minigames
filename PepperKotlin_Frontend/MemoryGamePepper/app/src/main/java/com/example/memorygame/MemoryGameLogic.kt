package com.example.memorygame

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class MemoryCard(val id: Int, val image: Int, var isFlipped: Boolean = false, var isMatched: Boolean = false) {
    var isFlippedState by mutableStateOf(isFlipped)
    var isMatchedState by mutableStateOf(isMatched)
}

val cardImages = listOf(
    R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4,
    R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8
)

fun createMemoryDeck(): List<MemoryCard> {
    val cards = cardImages.flatMap { listOf(MemoryCard(it.hashCode(), it), MemoryCard(it.hashCode(), it)) }
    return cards.shuffled()
}
