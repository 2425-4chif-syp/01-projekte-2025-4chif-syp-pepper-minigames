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
            "Hallo! Hast du schon alle Mahlzeiten eingetragen?",
            "Hey, schoen dich zu sehen. Wollen wir deine Mahlzeiten checken?",
            "Hallo zusammen! Lass uns schauen, was heute noch zu bestellen ist."
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
            "Hallo $personName! Was moechtest du heute essen?",
            "Hi $personName, schoen dass du da bist. Worauf hast du heute Lust?",
            "Willkommen $personName! Dann schauen wir gleich dein Essen an."
        )
    )

    fun noFaceDetected(): String = rotate(
        key = "no_face",
        options = listOf(
            "Huch, ich konnte dein Gesicht nicht gut sehen. Probieren wir es nochmal?",
            "Ups, das Bild war zu unklar. Lass uns gleich einen neuen Versuch machen.",
            "Ich habe dich gerade nicht sauber erkannt. Nochmal bitte."
        )
    )

    fun unknownPerson(): String = rotate(
        key = "unknown_person",
        options = listOf(
            "Oh, ich kenne dich leider noch nicht. Bitte kurz beim Betreuer anmelden.",
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
