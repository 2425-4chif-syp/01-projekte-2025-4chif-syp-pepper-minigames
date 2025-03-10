package com.example.memorygame

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


data class MemoryCard(val id: Int, val image: Int, var isFlipped: Boolean = false, var isMatched: Boolean = false)

val cardImages = listOf(
    R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4,
    R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8
)

fun createMemoryDeck(): List<MemoryCard> {
    val cards = cardImages.flatMap { listOf(MemoryCard(it.hashCode(), it), MemoryCard(it.hashCode(), it)) }
    return cards.shuffled()
}

class GameLogic {
    var flippedCards by mutableStateOf(listOf<MemoryCard>())
    var matchedCards by mutableStateOf(mutableSetOf<MemoryCard>())

    fun flipCard(card: MemoryCard, allCards: MutableList<MemoryCard>) {
        if (!card.isFlipped && flippedCards.size < 2) {
            card.isFlipped = true
            flippedCards = flippedCards + card

            if (flippedCards.size == 2) {
                val firstCard = flippedCards[0]
                val secondCard = flippedCards[1]

                if (firstCard.image == secondCard.image) {
                    firstCard.isMatched = true
                    secondCard.isMatched = true
                    matchedCards.add(firstCard)
                    matchedCards.add(secondCard)

                } else {
                    // Falsches Paar, Karten zurückdrehen
                    firstCard.isFlipped = false
                    secondCard.isFlipped = false
                }

                flippedCards = listOf() // Karten zurücksetzen
            }
        }
    }

    fun restartGame(): MutableList<MemoryCard> {
        flippedCards = listOf()
        matchedCards = mutableSetOf()
        return createMemoryDeck().toMutableList()
    }
}


/*data class MemoryCard(val id: Int, val image: Int, var isFlipped: Boolean = false, var isMatched: Boolean = false) {
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
var cardId = 0
fun createMemoryDeck(): List<MemoryCard> {
    val cards = cardImages.flatMap {
        listOf(
            MemoryCard(cardId++, it),
            MemoryCard(cardId++, it)
        )
    }
    return cards.shuffled()
}*/
