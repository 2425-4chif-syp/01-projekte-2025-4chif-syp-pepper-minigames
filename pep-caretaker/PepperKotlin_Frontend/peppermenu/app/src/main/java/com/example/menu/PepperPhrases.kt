package com.example.menu

object PepperPhrases {
    private val counters = mutableMapOf<String, Int>()

    @Synchronized
    private fun rotate(key: String, options: List<String>): String {
        if (options.isEmpty()) return ""
        val idx = (counters[key] ?: 0) % options.size
        counters[key] = idx + 1
        return options[idx]
    }

    fun cameraGreeting(): String = rotate(
        key = "camera_greeting",
        options = listOf(
            "Hallo!?",
            "Hey, schoen dich zu sehen.",
            "Hey!",
            "Grueß dich",
            "Servus"
        )
    )

    fun identityThinking(): String = rotate(
        key = "identity_thinking",
        options = listOf(
            "Ich muss kurz ueberlegen, wer du bist.",
            "Einen kleinen Moment, ich erkenne dich gleich.",
            "Kurz stillhalten bitte, ich pruefe gerade dein Gesicht."
        )
    )

    fun authSuccess(personName: String): String = rotate(
        key = "auth_success",
        options = listOf(
            "Hallo $personName! Was sollen wir beide machen?",
            "Hi $personName, schoen dass du da bist. Worauf hast du heute Lust?",
            "Willkommen $personName! Sollen wir ein Spiel spielen oder willst du dein Essensplan anschauen?"
        )
    )

    fun noFaceDetected(): String = rotate(
        key = "no_face",
        options = listOf(
            "Huch, ich konnte dein Gesicht nicht gut sehen. Bitte wähle deinen Namen aus!",
            "Ups, das Bild war zu unklar. Wähle deinen Namen aus der Liste aus!",
            "Ich habe dich gerade nicht sauber erkannt. Wähle deinen Namen aus der Liste aus!"
        )
    )

    fun unknownPerson(): String = rotate(
        key = "unknown_person",
        options = listOf(
            "Oh, ich kenne dich leider noch nicht. Bitte kurz beim Betreuer anmelden!",
            "Du bist noch nicht registriert. Ein Betreuer hilft dir sofort weiter.",
            "Ich finde dich noch nicht in meiner Liste. Bitte kurz anmelden lassen."
        )
    )

    fun connectionIssue(): String = rotate(
        key = "connection_issue",
        options = listOf(
            "Tut mir leid, ich habe gerade Verbindungsprobleme.",
            "Oh nein, die Verbindung hakt gerade ein bisschen.",
            "Ich komme gerade nicht sauber zum Server durch."
        )
    )
}
