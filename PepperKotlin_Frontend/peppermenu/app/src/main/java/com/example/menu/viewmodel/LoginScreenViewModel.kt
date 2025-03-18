package com.example.menu.viewmodel

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.menu.RoboterActions
import com.example.menu.dto.Person
import com.example.menu.network.HttpInstance
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class LoginScreenViewModel(application: Application) : AndroidViewModel(application) {

    //Zustand für den ausgewählten Namen
    var selectedName = mutableStateOf("Hermine Mayer")
        private set

    var persons : List<Person>? = null

    var names = listOf<String>(
        "Hermine Mayer", "Max Mustermann", "Anna Müller",
        "John Doe", "Max MusterMann", "Marc Laros"
    )

    private val context = application.applicationContext
    private var speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)


    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    init {

        viewModelScope.launch {
            persons = HttpInstance.getPersons()
            Log.d("Persons","${persons}")
            names = persons!!.map { p -> p.firstName + " " + p.lastName }
            Log.d("names","${names}")

        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                Log.d("Spracherkennung", "Fehler: $error")
            }

            override fun onResults(results: Bundle?) {
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                Log.d("Sprache","${data}")

                val answerContext =
                    "Bitte sag mir den Namen, welcher grad erwähnt wurde! Nur der Name bitte keine extra Wörter!"

                viewModelScope.launch {
                    try {
                        val response =
                            HttpInstance.sendPostRequestSmallTalk(data.toString() + answerContext)
                        val answer = response ?: "Fehler bei der API-Anfrage"

                        if (answer.isNotEmpty()) {
                            withContext(Dispatchers.Main) {
                                selectedName.value = answer
                                RoboterActions.speak("Sind Sie ${selectedName.value}?")
                            }
                        }
                    } catch (e: Exception) {
                        RoboterActions.speak("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                        Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startSpeechRecognition() {

        viewModelScope.launch(Dispatchers.Main) {
            speechRecognizer.startListening(speechRecognizerIntent)
        }
    }

    fun setName(name: String) {
        selectedName.value = name
    }

    fun captureAndRecognizePerson() {
        viewModelScope.launch(Dispatchers.IO) {
            val capturedImageDeferred = CompletableDeferred<ImageBitmap>()

            try {
                RoboterActions.speak("Ich mache kurz ein Foto von dir")

                // Warte auf das Bild
                RoboterActions.takePicture { image ->
                    capturedImageDeferred.complete(image)
                }

                // Hier wird gewartet, bis das Bild verfügbar ist
                val capturedImage = capturedImageDeferred.await()

                val response = HttpInstance.sendPostRequestImage(capturedImage)

                withContext(Dispatchers.Main) {
                    if (isResponseValid(response)) {
                        val parts = response.split(':')
                        if (parts.size > 1) {
                            selectedName.value = parts[1]
                            RoboterActions.speak("Sind Sie ${selectedName.value}?")
                        } else {
                            RoboterActions.speak("Tut mir Leid. Ich kann Sie leider nicht erkennen.")
                        }
                    } else {
                        RoboterActions.speak("Tut mir Leid. Ich kann Sie leider nicht erkennen.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    RoboterActions.speak("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                    Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
                }
            }
        }
    }

    private fun isResponseValid(response: String): Boolean {
        val responseUpper = response.uppercase(Locale.getDefault())
        return responseUpper != "" &&
                responseUpper != "NO MATCHING PERSON FOUND" &&
                responseUpper != "ERROR PROCESSING IMAGE"
    }

    fun testConnection() {
        viewModelScope.launch {
            try {
                val response = HttpInstance.sendPostRequestSmallTalk(
                    "Hallo ich heisse Nikola Mladenovic! Ich bin 19 Jahre alt und liebe Quarkus.\n" +
                            "Bitte sag mir den Namen, welcher grad erwähnt wurde! Nur das keine extra Wörter!"
                )

                val answer = response.takeIf { it.isNotEmpty() } ?: "Fehler bei der API-Anfrage"
                Log.d("Antwort", ": $answer")

                if (answer.isNotEmpty() && answer != "Fehler bei der API-Anfrage") {
                    selectedName.value = answer
                    RoboterActions.speak("Sind Sie ${selectedName.value}?")
                    Log.d("Antwort", "API richtig")
                }
            } catch (e: Exception) {
                Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
            }
        }
    }
}