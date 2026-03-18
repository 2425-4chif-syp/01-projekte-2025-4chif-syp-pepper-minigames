package com.example.menu.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menu.PepperPhrases
import com.example.menu.RoboterActions
import com.example.menu.dto.Person
import com.example.menu.network.HttpInstance
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class InitialFaceRecognitionViewModel : ViewModel() {

    val isLoading = mutableStateOf(false)
    val foundPerson = mutableStateOf("")
    val errorMessage = mutableStateOf<String?>(null)
    val hasError = mutableStateOf(false)
    val requiresManualSelection = mutableStateOf(false)

    private val hasSpokenToPerson = mutableStateOf(false)
    private var previousHumanAwareness: Any? = null
    private var persons: List<Person> = emptyList()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            ensurePersonsLoaded()
        }
    }

    fun talkToPerson() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentHumanAwareness = RoboterActions.getHumanAwarness()

            if (previousHumanAwareness == null && currentHumanAwareness != null) {
                hasSpokenToPerson.value = false
            }

            if (currentHumanAwareness != null && !hasSpokenToPerson.value) {
                hasSpokenToPerson.value = true
                RoboterActions.speak(PepperPhrases.cameraGreeting())
            }

            previousHumanAwareness = currentHumanAwareness
        }
    }

    fun clearError() {
        errorMessage.value = null
        hasError.value = false
        requiresManualSelection.value = false
    }

    fun takePicture(onAuthenticationSuccess: (Person) -> Unit) {
        isLoading.value = true
        errorMessage.value = null
        hasError.value = false
        requiresManualSelection.value = false

        viewModelScope.launch(Dispatchers.IO) {
            val capturedImageDeferred = CompletableDeferred<ImageBitmap>()

            try {
                ensurePersonsLoaded()
                RoboterActions.speak(PepperPhrases.identityThinking())
                delay(3000)

                RoboterActions.takePicture { image ->
                    capturedImageDeferred.complete(image)
                }

                val capturedImage = capturedImageDeferred.await()
                val response = HttpInstance.sendPostRequestImage(capturedImage)
                Log.d("InitialFaceRecognition", "Response=$response")

                withContext(Dispatchers.Main) {
                    val recognizedPerson = resolvePersonByResponse(response)
                    if (recognizedPerson != null && isResponseValid(response)) {
                        val fullName = "${recognizedPerson.firstName} ${recognizedPerson.lastName}".trim()
                        foundPerson.value = fullName
                        delay(2000)
                        onAuthenticationSuccess(recognizedPerson)
                        RoboterActions.speak(PepperPhrases.authSuccess(fullName))
                    } else {
                        handleRecognitionError(response)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage.value = "Drücken Sie die obere Taste um sich anzumelden"
                    hasError.value = true
                    delay(1000)
                    RoboterActions.speak(PepperPhrases.connectionIssue())
                    Log.e("InitialFaceRecognition", "Fehler beim API-Aufruf: ${e.message}")
                }
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun handleRecognitionError(response: String) {
        if (
            response.contains("Error processing image", ignoreCase = true) ||
            response.contains("no faces in the image", ignoreCase = true)
        ) {
            errorMessage.value = "Kein Gesicht erkannt - Weiter zur Namensliste."
            hasError.value = true
            requiresManualSelection.value = true
            viewModelScope.launch(Dispatchers.IO) {
                RoboterActions.speak(PepperPhrases.noFaceDetected())
            }
            return
        }

        errorMessage.value = "Ich kenne dich noch nicht - Bitte melde dich bei einem Betreuer an."
        hasError.value = true
        requiresManualSelection.value = true
        viewModelScope.launch(Dispatchers.IO) {
            RoboterActions.speak(PepperPhrases.unknownPerson())
        }
    }

    private suspend fun ensurePersonsLoaded() {
        if (persons.isNotEmpty()) return
        persons = HttpInstance.getPersons()
    }

    private fun resolvePersonByResponse(response: String): Person? {
        val normalizedResponse = normalizeName(response)
        if (normalizedResponse.isBlank()) return null

        return persons.firstOrNull {
            normalizeName("${it.firstName} ${it.lastName}")
                .equals(normalizedResponse, ignoreCase = true)
        }
    }

    private fun normalizeName(value: String): String {
        return value
            .replace("\"", "")
            .replace("\n", " ")
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
