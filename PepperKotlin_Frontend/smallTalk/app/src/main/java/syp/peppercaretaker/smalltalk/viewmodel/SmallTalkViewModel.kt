package syp.peppercaretaker.smalltalk.viewmodel

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
import syp.peppercaretaker.smalltalk.PepperFuncs
import syp.peppercaretaker.smalltalk.model.Api
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SmallTalkViewModel(application: Application): AndroidViewModel(application) {

    var isLoading = mutableStateOf(false)

    // Config für Spracherkennung
    private val context = application.applicationContext
    private var speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)


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
                Log.d("Sprache","${data}")

                isLoading.value = true

                viewModelScope.launch {
                    try {
                        val response = Api.sendPostRequestSmallTalk(data.toString())

                        if(response != "" && response != null){
                            withContext(Dispatchers.Main) {
                                PepperFuncs.speakAsync("${response}")
                            }
                        }
                        else{
                            PepperFuncs.speakAsync("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                        }
                    } catch (e: Exception) {
                        PepperFuncs.speakAsync("Tut mir Leid. Ich kann sie leider nicht erkennen.")
                        Log.e("API-Fehler", "Fehler beim API-Aufruf: ${e.message}")
                    }
                    finally {
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

    fun testConnection(){
        viewModelScope.launch {
            try {
                val response = Api.sendPostRequestSmallTalk("Was ist 5x5?")

                if(response != "" && response != null){
                    withContext(Dispatchers.Main){
                        Log.d("Response","${response}")
                    }
                }
                else
                {
                    Log.d("Response","Leere Response")
                }


            }catch (e:Exception)
            {
                Log.d("Respone","${e.message}")
            }
        }
    }
}