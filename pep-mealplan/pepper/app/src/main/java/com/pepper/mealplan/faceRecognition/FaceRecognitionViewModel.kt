package com.pepper.mealplan.faceRecognition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import android.util.Log
import kotlinx.coroutines.*
import com.pepper.mealplan.RoboterActions
import com.pepper.mealplan.network.HttpInstance
import java.util.*

class FaceRecognitionViewModel : ViewModel(){
    
    val isLoading = mutableStateOf(false)
    val foundPerson = mutableStateOf("")
    val errorMessage = mutableStateOf<String?>(null)
    val hasError = mutableStateOf(false)
    private var hasSpokenToPerson = mutableStateOf(false)
    private var previousHumanAwareness: Any? = null
    private var onAuthenticationSuccess: (() -> Unit)? = null
    
    fun setOnAuthenticationSuccess(callback: () -> Unit) {
        onAuthenticationSuccess = callback
    }
    
    fun talkToPerson(){
        viewModelScope.launch(Dispatchers.IO) {
            val currentHumanAwareness = RoboterActions.getHumanAwarness()

            if (previousHumanAwareness == null && currentHumanAwareness != null) {
                hasSpokenToPerson.value = false
            }
            
            if(currentHumanAwareness != null && !hasSpokenToPerson.value){
                hasSpokenToPerson.value = true
                RoboterActions.speak("Haben Sie schon Ihre Mahlzeiten eingetragen?")
            }
            
            previousHumanAwareness = currentHumanAwareness
        }
    }

    fun clearError() {
        errorMessage.value = null
        hasError.value = false
    }

    fun takePicture(){
        isLoading.value = true
        errorMessage.value = null  // Reset error message
        hasError.value = false
        viewModelScope.launch(Dispatchers.IO) {
            val capturedImageDeferred = CompletableDeferred<ImageBitmap>()

            try {
                RoboterActions.speak("Ich mache kurz ein Foto von Ihnen")

                delay(3000)

                RoboterActions.takePicture { image ->
                    capturedImageDeferred.complete(image)
                }

                val capturedImage = capturedImageDeferred.await()

                val response = HttpInstance.sendPostRequestImage(capturedImage)
                Log.d("Response", "${response}")

                withContext(Dispatchers.Main) {
                    if (isResponseValid(response) && response != "" && response != null) {
                        foundPerson.value = response

                        delay(2000)
                        onAuthenticationSuccess?.invoke()
                        RoboterActions.speak("Hallo " + response + " Was wollen Sie essen?")

                    } else {
                        // Behandle Server-Errors als Fehler
                        if (response.contains("Error processing image", ignoreCase = true) || 
                            response.contains("no faces in the image", ignoreCase = true)) {
                            errorMessage.value = "Kein Gesicht erkannt - Drücken Sie die obere Taste um es erneut zu versuchen"
                            hasError.value = true
                        }
                        delay(1000)
                        RoboterActions.speak("Tut mir Leid. Ich kann Sie leider nicht erkennen.")
                        isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage.value = "Drücken Sie die obere Taste um sich anzumelden"
                    hasError.value = true
                    delay(1000)
                    RoboterActions.speak("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                    Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
                    isLoading.value = false
                }
            }
        }
    }

    private fun isResponseValid(response: String): Boolean {
        val responseUpper = response.uppercase(Locale.getDefault())
        return responseUpper != "" && responseUpper != "NO MATCHING PERSON FOUND" && responseUpper != "ERROR PROCESSING IMAGE"
    }
}