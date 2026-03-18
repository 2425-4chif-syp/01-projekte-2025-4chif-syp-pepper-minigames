package com.example.menu.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.menu.RoboterActions
import com.example.menu.dto.Person
import com.example.menu.network.HttpInstance
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.util.Locale

class LoginScreenViewModel(application: Application) : AndroidViewModel(application) {

    var selectedName = mutableStateOf("")
        private set

    var selectedGender = mutableStateOf("")
        private set

    var selectedPerson = mutableStateOf<Person?>(null)
        private set

    var persons: List<Person>? = null
        private set

    var names = mutableStateOf<List<String>>(emptyList())
        private set

    var filteredPersons = mutableStateOf<List<Person>>(emptyList())
        private set

    var searchQuery = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false)

    val answerContext =
        "Bitte sag mir den Namen, welcher grad erwaehnt wurde! Nur der Name bitte keine extra Woerter!"

    private var allPersons: List<Person> = emptyList()

    private val context = application.applicationContext
    private var speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    init {
        loadPersons()

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
                Log.d("Sprache", "$data")
                isLoading.value = true

                viewModelScope.launch {
                    try {
                        val response = HttpInstance.sendPostRequestSmallTalk(data.toString() + answerContext)
                        if (response.isNotBlank()) {
                            withContext(Dispatchers.Main) {
                                findRightPerson(response)
                            }
                        } else {
                            RoboterActions.speak("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                        }
                    } catch (e: Exception) {
                        RoboterActions.speak("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                        Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
                    } finally {
                        isLoading.value = false
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

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
        if (query.isBlank()) {
            applySearchFilter()
        }
    }

    fun applySearchFilter() {
        val filtered = filterPersonsByQuery(searchQuery.value)
        filteredPersons.value = filtered
        names.value = filtered.map(::fullName)
    }

    fun findRightPerson(response: String) {
        val rightPerson = findPersonFromText(response)
        if (rightPerson == null) {
            RoboterActions.speak("Ich konnte keine richtige Person finden!")
        } else {
            selectPerson(rightPerson)
            RoboterActions.speak("Sind Sie ${rightPerson.firstName} ${rightPerson.lastName}")
        }
    }

    fun selectPerson(person: Person) {
        selectedPerson.value = person
        setName(fullName(person))
        setGender(person.gender)
    }

    fun setName(name: String) {
        selectedName.value = name
    }

    fun setGender(gender: Boolean) {
        selectedGender.value = if (gender) "Mann" else "Frau"
    }

    fun captureAndRecognizePerson() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val capturedImageDeferred = CompletableDeferred<ImageBitmap>()

            try {
                RoboterActions.speak("Ich mache kurz ein Foto von dir")

                RoboterActions.takePicture { image ->
                    capturedImageDeferred.complete(image)
                }

                val capturedImage = capturedImageDeferred.await()
                val response = HttpInstance.sendPostRequestImage(capturedImage)
                Log.d("Response", response)

                withContext(Dispatchers.Main) {
                    if (isResponseValid(response) && response.isNotBlank()) {
                        findRightPerson(response)
                    } else {
                        RoboterActions.speak("Tut mir Leid. Ich kann Sie leider nicht erkennen.")
                    }
                }
            } catch (e: Exception) {
                RoboterActions.speak("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun loadPersons() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                allPersons = HttpInstance.getPersons()
                persons = allPersons
                filteredPersons.value = allPersons
                names.value = allPersons.map(::fullName)
            } catch (e: Exception) {
                Log.e("Names", "Error loading names: ${e.message}")
                allPersons = emptyList()
                persons = emptyList()
                filteredPersons.value = emptyList()
                names.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun filterPersonsByQuery(query: String): List<Person> {
        val normalizedQuery = normalizeForSearch(query)
        if (normalizedQuery.isBlank()) {
            return allPersons
        }

        val tokens = normalizedQuery.split(" ").filter { it.isNotBlank() }
        return allPersons.mapNotNull { person ->
            val normalizedName = normalizeForSearch(fullName(person))
            val score = matchScore(normalizedName, normalizedQuery, tokens)
            if (score >= 0) person to score else null
        }.sortedWith(
            compareByDescending<Pair<Person, Int>> { it.second }
                .thenBy { normalizeForSearch(fullName(it.first)) }
        ).map { it.first }
    }

    private fun matchScore(normalizedName: String, normalizedQuery: String, tokens: List<String>): Int {
        var score = 0

        for (token in tokens) {
            score += when {
                normalizedName == token -> 120
                normalizedName.startsWith(token) -> 90
                normalizedName.contains(" $token") -> 70
                normalizedName.contains(token) -> 50
                else -> return -1
            }
        }

        if (normalizedName == normalizedQuery) score += 80
        if (normalizedName.startsWith(normalizedQuery)) score += 50
        if (normalizedName.contains(normalizedQuery)) score += 30
        return score
    }

    private fun findPersonFromText(response: String): Person? {
        val normalizedResponse = normalizeForSearch(response)
        if (normalizedResponse.isBlank()) {
            return null
        }

        return allPersons.firstOrNull {
            normalizeForSearch(fullName(it)) == normalizedResponse
        } ?: allPersons.firstOrNull {
            normalizeForSearch(fullName(it)).contains(normalizedResponse)
        }
    }

    private fun fullName(person: Person): String {
        return "${person.firstName} ${person.lastName}".trim()
    }

    private fun normalizeForSearch(value: String): String {
        val withoutQuotes = value.replace("\"", " ").replace("\n", " ")
        val decomposed = Normalizer.normalize(withoutQuotes, Normalizer.Form.NFD)
        return decomposed
            .replace("\\p{M}+".toRegex(), "")
            .lowercase(Locale.getDefault())
            .replace("[^a-z0-9\\s]".toRegex(), " ")
            .trim()
            .replace(Regex("\\s+"), " ")
    }

    private fun isResponseValid(response: String): Boolean {
        val responseUpper = response.uppercase(Locale.getDefault())
        return responseUpper.isNotBlank() &&
            responseUpper != "NO MATCHING PERSON FOUND" &&
            responseUpper != "ERROR PROCESSING IMAGE"
    }
}
