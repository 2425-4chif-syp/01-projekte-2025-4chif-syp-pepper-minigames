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
    private var onAuthenticationSuccess: (() -> Unit)? = null
    
    fun setOnAuthenticationSuccess(callback: () -> Unit) {
        onAuthenticationSuccess = callback
    }
    
    fun talkToPerson(){
        viewModelScope.launch(Dispatchers.IO) {
            if(RoboterActions.getHumanAwarness() != null){
                RoboterActions.speak("Haben Sie schon Ihre Mahlzeiten eingetragen?")
            }
        }
    }

    fun takePicture(){
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val capturedImageDeferred = CompletableDeferred<ImageBitmap>()

            try {
                RoboterActions.speak("Ich mache kurz ein Foto von Ihnen")
                
                // Wait a bit to ensure the speech finishes
                delay(3000)

                // Warte auf das Bild
                RoboterActions.takePicture { image ->
                    capturedImageDeferred.complete(image)
                }

                val capturedImage = capturedImageDeferred.await()

                val response = HttpInstance.sendPostRequestImage(capturedImage)
                Log.d("Response", "${response}")

                withContext(Dispatchers.Main) {
                    if (isResponseValid(response) && response != "" && response != null) {
                        foundPerson.value = response
                        
                        // Wait a moment before speaking the greeting
                        delay(1000)
                        RoboterActions.speak("Hallo " + response + " Was wollen Sie essen?")
                        
                        // Wait for greeting to finish, then call success callback
                        delay(3000)
                        onAuthenticationSuccess?.invoke()

                    } else {
                        delay(1000)
                        RoboterActions.speak("Tut mir Leid. Ich kann Sie leider nicht erkennen.")
                    }
                }
            } catch (e: Exception) {
                delay(1000)
                RoboterActions.speak("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
            }
            finally {
                isLoading.value = false
            }
        }
    }

    private fun isResponseValid(response: String): Boolean {
        val responseUpper = response.uppercase(Locale.getDefault())
        return responseUpper != "" && responseUpper != "NO MATCHING PERSON FOUND" && responseUpper != "ERROR PROCESSING IMAGE"
    }
}