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
import com.example.menu.network.ApiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class LoginScreenViewModel(application: Application) : AndroidViewModel(application) {

    //Zustand für den ausgewählten Namen
    var selectedName = mutableStateOf("Hermine Mayer")
        private set

    val names = listOf<String>(
        "Hermine Mayer", "Max Mustermann", "Anna Müller",
        "John Doe", "Max MusterMann", "Marc Laros"
    )

    private val context = application.applicationContext
    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)


    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    init {
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
                val answerContext =
                    "Bitte sag mir den Namen, welcher grad erwähnt wurde! Nur das keine extra Wörter!"

                viewModelScope.launch {
                    try {
                        val response =
                            ApiHelper.sendPostRequestSmallTalk(data.toString() + answerContext)
                        val answer = response.ifEmpty { "Fehler bei der API-Anfrage" }

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
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    fun setName(name: String) {
        selectedName.value = name
    }

    fun captureAndRecognizePerson(){
        viewModelScope.launch {
            var image: ImageBitmap? = null

            try{
                RoboterActions.speak("Ich mache kurz ein Foto von dir")
                RoboterActions.takePicture { image }

                if(image != null){
                    val response = ApiHelper.sendPostRequest(image)

                    if(IsResponseValid(response = response)){
                        selectedName.value = response.split(':')[1]
                        RoboterActions.speak("Sind Sie ${selectedName}")
                    }
                    else{
                        RoboterActions.speak("Tut mir Leid. Ich kann Sie leider nicht erkennen")
                    }
                }

            }catch (e: Exception){
                RoboterActions.speak("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
            }

        }
    }

    fun IsResponseValid(response: String): Boolean{
        val responseUpper = response.uppercase(Locale.getDefault())
        return responseUpper!= "" && responseUpper != "NO MATCHING PERSON FOUND" && responseUpper != "TODO!!!!!!!!!!!!!!!";
    }

    fun testConntection(){
        viewModelScope.launch {
            try {
                val response =
                    ApiHelper.sendPostRequestSmallTalk("Hallo ich heisse Nikola Mladenovic! Ich bin 19 Jahre alt und liebe Quarkus.\n Bitte sag mir den Namen, welcher grad erwähnt wurde! Nur das keine extra Wörter!")
                val answer = response.ifEmpty { "Fehler bei der API-Anfrage" }

                if (answer.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        selectedName.value = answer
                        RoboterActions.speak("Sind Sie ${selectedName.value}?")
                        Log.d("Antwort","API richtig")
                    }
                }
            } catch (e: Exception) {
                Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
            }
        }
    }
}