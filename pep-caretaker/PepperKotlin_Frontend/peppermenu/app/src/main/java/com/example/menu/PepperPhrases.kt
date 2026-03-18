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
            "Hey du, schoen dich zu sehen.",
            "Hi, stell dich kurz vor mich, dann legen wir los.",
            "Servus, ich erkenne dich gleich."
        )
    )

    fun identityThinking(): String = rotate(
        key = "identity_thinking",
        options = listOf(
            "Ich schaue kurz, ob ich dich erkenne.",
            "Einen kleinen Moment, ich prüfe gerade dein Gesicht.",
            "Bleib kurz still, ich bin gleich fertig."
        )
    )

    fun authSuccess(personName: String): String = rotate(
        key = "auth_success",
        options = listOf(
            "Hallo $personName, schön dass du da bist.",
            "Perfekt $personName, du bist angemeldet.",
            "Super $personName, wir können starten."
        )
    )

    fun noFaceDetected(): String = rotate(
        key = "no_face",
        options = listOf(
            "Ich konnte dein Gesicht gerade nicht gut sehen. Wähle bitte deinen Namen aus.",
            "Das Bild war zu unklar. Such dir bitte deinen Namen in der Liste aus.",
            "Ich habe dich gerade nicht sauber erkannt. Wir machen einfach über die Namensliste weiter."
        )
    )

    fun unknownPerson(): String = rotate(
        key = "unknown_person",
        options = listOf(
            "Ich kenne dich noch nicht. Meld dich bitte kurz bei einer Betreuungsperson an.",
            "Du bist noch nicht registriert. Eine Betreuungsperson hilft dir sofort.",
            "Ich finde dich noch nicht in meiner Liste. Bitte kurz anmelden lassen."
        )
    )

    fun connectionIssue(): String = rotate(
        key = "connection_issue",
        options = listOf(
            "Ups, gerade habe ich ein kleines Verbindungsproblem.",
            "Meine Verbindung hakt kurz, versuch es bitte gleich nochmal.",
            "Ich komme gerade nicht gut zum Server durch."
        )
    )

    fun manualFaceRetryStart(): String = rotate(
        key = "manual_face_retry_start",
        options = listOf(
            "Super, ich probiere die Gesichtserkennung nochmal für dich.",
            "Alles klar, ich mache direkt einen neuen Erkennungsversuch.",
            "Sehr gerne, wir versuchen die Gesichtserkennung nochmal."
        )
    )

    fun manualFaceRetryFailed(): String = rotate(
        key = "manual_face_retry_failed",
        options = listOf(
            "Ich konnte dich leider noch nicht sicher erkennen. Wähle bitte deinen Namen.",
            "Das hat noch nicht geklappt, aber kein Problem. Tippe einfach deinen Namen an.",
            "Ich erkenne dich noch nicht eindeutig. Nimm bitte die Namensliste."
        )
    )

    fun menuWelcome(personName: String?): String {
        val cleanName = personName?.trim().orEmpty()
        return if (cleanName.isNotBlank()) {
            rotate(
                key = "menu_welcome_named",
                options = listOf(
                    "Hey $cleanName, was möchtest du heute machen?",
                    "Schoen, dass du da bist $cleanName. Lust auf Spiel, Geschichte oder Essensplan?",
                    "Hi $cleanName, ich bin bereit. Womit wollen wir starten?"
                )
            )
        } else {
            rotate(
                key = "menu_welcome_generic",
                options = listOf(
                    "Hey, was möchtest du heute machen?",
                    "Schoen, dass du da bist. Worauf hast du Lust?",
                    "Ich bin bereit für dich. Was darf es sein?"
                )
            )
        }
    }

    fun menuSmallTalkIntro(): String = rotate(
        key = "menu_smalltalk_intro",
        options = listOf(
            "Magst du ein bisschen plaudern? Ich habe ein paar kleine Ideen.",
            "Wenn du willst, können wir kurz plaudern.",
            "Ich habe was Nettes für dich: Witz, Wissen oder Bewegung."
        )
    )

    fun smallTalkJoke(): String = rotate(
        key = "smalltalk_joke",
        options = listOf(
            "Warum trinken Kühe keine Milch? Weil sie sie selber geben.",
            "Warum hat der Besen gute Laune? Weil er immer geschniegelt ist.",
            "Was macht der Apfel im Fitnessstudio? Er trainiert seine Kernmuskeln.",
            "Warum ging die Tomate über die Straße? Weil sie ketchup wollte.",
            "Was macht ein Hund ohne Beine? Egal, wie man ihn ruft, er kommt nicht.",
            "Warum können Fische so gut rechnen? Weil sie immer in Schwärmen unterwegs sind.",
            "Was macht eine Wolke, wenn sie juckt? Sie sucht ein Kratzgebirge.",
            "Warum lachen Blumen so gern? Weil die Sonne sie kitzelt.",
            "Was ist orange und läuft durch den Wald? Eine Wanderine.",
            "Warum gehen Uhren nie unter? Weil sie immer weiterlaufen.",
            "Was macht ein Ei am Computer? Es drückt die Enter-Taste.",
            "Warum können Vögel so gut singen? Weil sie den Text immer pfeifen."
        )
    )

    fun smallTalkFact(): String = rotate(
        key = "smalltalk_fact",
        options = listOf(
            "Wusstest du schon? Ein freundliches Hallo macht oft den ganzen Tag besser.",
            "Kleiner Fun Fact: Lachen entspannt den Körper in wenigen Sekunden.",
            "Heute schon gewusst? Ein kurzer Spaziergang kann die Stimmung direkt heben.",
            "Wusstest du schon? Ein Lächeln kann ansteckend sein.",
            "Kleiner Gedanke für heute: Freundliche Worte tun oft beiden gut.",
            "Heute schon gewusst? Schon ein paar Minuten Bewegung können gut tun.",
            "Wusstest du schon? Musik kann schöne Erinnerungen wecken.",
            "Kleiner Fun Fact: Frische Luft tut oft Körper und Kopf gut.",
            "Heute schon gewusst? Ein nettes Gespräch kann den Tag verschönern.",
            "Wusstest du schon? Gute Laune beginnt oft mit kleinen Dingen.",
            "Kleiner Gedanke: Ein ruhiger Moment kann richtig gut tun.",
            "Heute schon gewusst? Trinken ist wichtig, auch wenn man keinen großen Durst hat.",
            "Wusstest du schon? Tageslicht kann die Stimmung heben.",
            "Kleiner Fun Fact: Gemeinsames Lachen verbindet Menschen.",
            "Heute schon gewusst? Eine kurze Pause kann neue Kraft geben.",
            "Wusstest du schon? Freundlichkeit kommt oft doppelt zurück.",
            "Kleiner Gedanke für heute: Ein schöner Moment darf ruhig langsam sein.",
            "Heute schon gewusst? Ein bisschen Bewegung jeden Tag ist oft schon viel wert.",
            "Wusstest du schon? Ein nettes Wort kann lange in Erinnerung bleiben.",
            "Kleiner Fun Fact: Ruhe und Erholung sind auch etwas sehr Wertvolles."
        )
    )

    fun smallTalkMovement(): String = rotate(
        key = "smalltalk_movement",
        options = listOf(
            "Mini Bewegungsidee: Schultern einmal hochziehen und locker fallen lassen.",
            "Kleine Übung: Atme tief ein, zaehle bis drei und langsam wieder aus.",
            "Wenn du magst: Streck einmal die Arme nach oben und lächele kurz."
        )
    )

    fun smallTalkCompliment(personName: String?): String {
        val cleanName = personName?.trim().orEmpty()
        val prefix = if (cleanName.isNotBlank()) "$cleanName, " else ""

        return rotate(
            key = if (cleanName.isNotBlank()) "smalltalk_compliment_named" else "smalltalk_compliment_generic",
            options = listOf(
                "${prefix}du hast eine richtig positive Ausstrahlung.",
                "${prefix}mit dir macht das hier gleich mehr Spass.",
                "${prefix}ich finde, du bringst eine tolle Energie mit.",
                "${prefix}du verbreitest gute Laune.",
                "${prefix}du bringst gleich eine schöne Stimmung mit.",
                "${prefix}ich finde, du wirkst richtig freundlich.",
                "${prefix}mit dir fühlt sich alles gleich angenehmer an.",
                "${prefix}du hast ein schönes Lächeln.",
                "${prefix}du bringst Wärme und Freundlichkeit mit.",
                "${prefix}ich freue mich, dass du da bist.",
                "${prefix}du machst das hier gleich viel netter.",
                "${prefix}du hast etwas sehr Herzliches an dir.",
                "${prefix}mit dir ist es gleich schöner.",
                "${prefix}du wirkst heute besonders gut gelaunt.",
                "${prefix}ich finde, du hast eine richtig angenehme Art.",
                "${prefix}du tust mit deiner Art einfach gut.",
                "${prefix}mit dir kommt gleich mehr Freude herein.",
                "${prefix}du bringst eine sehr schöne Ruhe mit.",
                "${prefix}ich mag deine freundliche Art.",
                "${prefix}du machst diesen Moment gleich ein bisschen schöner."
            )
        )
    }

    fun launchingApp(appName: String): String = rotate(
        key = "launching_app",
        options = listOf(
            "Alles klar, ich starte $appName für dich.",
            "Super, $appName geht sofort los.",
            "Sehr gerne, ich öffne jetzt $appName."
        )
    )
}
