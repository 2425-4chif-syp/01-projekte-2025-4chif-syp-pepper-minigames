package com.project_award

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import java.util.Locale

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {

    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var recognizerIntent: Intent
    private var isListening = false
    private var statusText by mutableStateOf("")
    private var lastHandledKeyword: String? = null
    private val recognitionLocale = Locale.ENGLISH
    private var recognizerAudioMuted = false
    private var previousSystemVolume: Int = -1

    private companion object {
        private const val SPEECH_VOLUME_PERCENT = 75
    }

    private val keywordResponses: List<Pair<List<String>, String>> = listOf(
        listOf("home") to "It is not a nursing home! It is a senior care center.",
        listOf("storytelling") to "Which includes speech , visuals and movement for an entertaining experience !",
        listOf("stories") to "Which includes speech , visuals and movement for an entertaining experience !",

    )

    private val requestMicPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.d(TAG, "RECORD_AUDIO granted")
            statusText = getString(R.string.status_microphone_active)
            startListening()
        } else {
            Log.e(TAG, "RECORD_AUDIO denied")
            statusText = getString(R.string.status_microphone_permission_missing)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity created")
        statusText = getString(R.string.status_preparing_microphone)
        QiSDK.register(this, this)
        Log.d(TAG, "QiSDK registered")

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hello),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            statusText = getString(R.string.status_speech_recognizer_unavailable)
            Log.e(TAG, "SpeechRecognizer is not available")
            return
        }

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, recognitionLocale.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, recognitionLocale.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, recognitionLocale.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(createRecognitionListener())
        }

        ensureMicPermissionAndStart()
    }

    override fun onDestroy() {
        stopListening(restoreRecognizerAudio = true)
        speechRecognizer?.destroy()
        speechRecognizer = null
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        Log.i(TAG, "Robot focus gained")
        RoboterActions.qiContext = qiContext
        RoboterActions.robotExecute = true
        Log.d("QiContext", "${RoboterActions.robotExecute} / ${RoboterActions.qiContext}")
        statusText = getString(R.string.status_robot_ready)
    }

    override fun onRobotFocusLost() {
        Log.w(TAG, "Robot focus lost")
        RoboterActions.robotExecute = false
        RoboterActions.qiContext = null
        statusText = getString(R.string.status_robot_focus_lost)
    }

    override fun onRobotFocusRefused(reason: String?) {
        Log.e(TAG, "Robot focus refused: $reason")
        RoboterActions.robotExecute = false
        RoboterActions.qiContext = null
        statusText = getString(R.string.status_robot_focus_refused, reason ?: "-")
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                Log.d(TAG, "Mic ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "Speech started")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d(TAG, "Speech ended")
            }

            override fun onError(error: Int) {
                isListening = false
                Log.w(TAG, "Speech recognition error: $error")
                resetKeywordTrigger()
                restartListening()
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).orEmpty()
                processMatches(matches, resetAfterProcessing = true)
                restartListening()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    .orEmpty()
                processMatches(matches)
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun processMatches(matches: List<String>, resetAfterProcessing: Boolean = false) {
        if (matches.isEmpty()) {
            if (resetAfterProcessing) {
                resetKeywordTrigger()
            }
            return
        }

        Log.d(TAG, "Recognized: $matches")
        val normalizedUtterances = matches.map { normalize(it) }
        val found = keywordResponses.firstOrNull { (keywords, _) ->
            keywords.any { keyword -> normalizedUtterances.any { utterance -> utterance.contains(keyword) } }
        }

        if (found != null) {
            val matchedKeyword = found.first.firstOrNull { keyword ->
                normalizedUtterances.any { utterance -> utterance.contains(keyword) }
            }

            if (matchedKeyword == null || matchedKeyword == lastHandledKeyword) {
                if (resetAfterProcessing) {
                    resetKeywordTrigger()
                }
                return
            }

            lastHandledKeyword = matchedKeyword
            val response = found.second
            statusText = getString(R.string.status_keyword_recognized, response)
            Log.i(TAG, "Keyword hit. Trigger response: $response")
            restoreRecognizerAudio()
            setSpeechVolumeToTarget()
            val future = RoboterActions.speak(response)
            Log.d(TAG, "RoboterActions.speak() called. Future=$future")
            if (future == null) {
                statusText = getString(R.string.status_keyword_no_robot_focus)
                Log.e(TAG, "Speak returned null (focus/context not ready)")
            }
        }

        if (resetAfterProcessing) {
            resetKeywordTrigger()
        }
    }

    private fun resetKeywordTrigger() {
        lastHandledKeyword = null
    }
    
    private fun normalize(input: String): String {
        return input.lowercase(recognitionLocale)
            .replace("ß", "ss")
            .replace("ü", "ue")
            .replace("ö", "oe")
            .replace("ä", "ae")
            .replace("[^a-z0-9 ]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }


    private fun ensureMicPermissionAndStart() {
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            statusText = getString(R.string.status_microphone_active)
            startListening()
        } else {
            requestMicPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startListening() {
        if (isListening) {
            return
        }
        try {
            muteRecognizerAudio()
            speechRecognizer?.startListening(recognizerIntent)
            isListening = true
            Log.d(TAG, "startListening() called")
        } catch (e: Exception) {
            isListening = false
            restoreRecognizerAudio()
            Log.e(TAG, "Failed to start listening", e)
        }
    }

    private fun restartListening() {
        stopListening(restoreRecognizerAudio = false)
        startListening()
    }

    private fun stopListening(restoreRecognizerAudio: Boolean = false) {
        if (!isListening) {
            if (restoreRecognizerAudio) {
                restoreRecognizerAudio()
            }
            return
        }
        try {
            speechRecognizer?.stopListening()
            isListening = false
            Log.d(TAG, "stopListening() called")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to stop listening", e)
        } finally {
            if (restoreRecognizerAudio) {
                restoreRecognizerAudio()
            }
        }
    }

    private fun muteRecognizerAudio() {
        if (recognizerAudioMuted) {
            return
        }

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        previousSystemVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)

        if (previousSystemVolume > 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0)
        }

        recognizerAudioMuted = true
    }

    private fun setSpeechVolumeToTarget() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (maxMusicVolume <= 0) {
            return
        }

        val targetMusicVolume = (maxMusicVolume * SPEECH_VOLUME_PERCENT) / 100
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetMusicVolume.coerceAtLeast(1), 0)
    }

    private fun restoreRecognizerAudio() {
        if (!recognizerAudioMuted) {
            return
        }

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (previousSystemVolume >= 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, previousSystemVolume, 0)
        }

        previousSystemVolume = -1
        recognizerAudioMuted = false
    }
}