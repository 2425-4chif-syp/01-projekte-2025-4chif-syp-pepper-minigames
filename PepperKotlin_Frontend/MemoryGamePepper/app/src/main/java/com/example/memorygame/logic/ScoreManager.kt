package com.example.memorygame.logic

class ScoreManager(rows: Int, columns: Int) {

    private var currentScore: Int = 0
    private val pointsPerMatch: Int
    private val pointsPerMistake: Int

    init {
        // Punkte basierend auf Grid-Größe definieren
        val gridPointsMap = mapOf(
            Pair(2, 3) to Pair(10, 2),
            Pair(2, 4) to Pair(15, 4),
            Pair(3, 4) to Pair(20, 6),
            Pair(4, 4) to Pair(25, 8)
        )

        // Punkte aus Map holen oder Standardwert verwenden (10 Punkte für Match, 3 Punkte für Fehler)
        val points = gridPointsMap[Pair(rows, columns)] ?: Pair(10, 3)

        pointsPerMatch = points.first
        pointsPerMistake = points.second
    }

    // Punkte erhöhen, wenn Paar gefunden wurde
    fun onMatchFound() {
        currentScore += pointsPerMatch
    }

    // Punkte reduzieren bei einem Fehler (kein passendes Paar)
    fun onMismatch() {
        currentScore -= pointsPerMistake
        if (currentScore < 0) currentScore = 0 // verhindert negative Punkte
    }

    // Aktuelle Punktzahl abrufen
    fun getCurrentScore(): Int = currentScore

    // Punkte zurücksetzen (neues Spiel)
    fun resetScore() {
        currentScore = 0
    }
}
