package com.example.mmg.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mmg.MainActivity
import com.example.mmg.RoboterActions
import com.example.mmg.dto.EmoteDto
import com.example.mmg.dto.MmgDto
import com.example.mmg.dto.StepDto
import com.example.mmg.dto.getEmotes
import com.example.mmg.network.HttpInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.util.*

class MmgViewModel() : ViewModel() {

    private val _mmgList = MutableStateFlow<List<MmgDto>>(emptyList())
    val mmgList = _mmgList.asStateFlow()

    private val _mmgSteps = MutableStateFlow<List<StepDto>>(emptyList())
    val mmgStep = _mmgSteps.asStateFlow()

    val imageBitMap = MutableStateFlow<ImageBitmap?>(null)

    val _stepCount = MutableStateFlow(0)

    val stepsFinished = MutableStateFlow<Boolean>(false)

    var emotes: List<EmoteDto> = emptyList()

    fun incrementStepCount() {
        _stepCount.value += 1
    }

    fun resetStepCount(){
        _stepCount.value = 0
    }

    fun displayStep(){

        Log.d("Count_Step","${_stepCount.value}")
        Log.d("Mmg_Count", "${_mmgSteps.value.size}")

        if(_stepCount.value < _mmgSteps.value.size){

            val stepDto: StepDto = _mmgSteps.value[_stepCount.value]

            Log.d("Action","${stepDto}")
            Log.d("Index","${_stepCount.value}")
            Log.d("Base64","${stepDto.imageBase64}")
            Log.d("Text", "${stepDto.text}")
            Log.d("Move","${stepDto.move}")
            Log.d("Duration","${stepDto.durationInSeconds}")

            if(stepDto.imageBase64 != null){

                imageBitMap.value = base64ToBitmap(stepDto.imageBase64!!)
            }

            RoboterActions.speak(stepDto.text)

            val emote = getEmote(stepDto = stepDto)

            if(emote != -1){
                RoboterActions.animation(emote)
            }
        }
        else{
            stepsFinished.value = true
            RoboterActions.speak("Die Geschichte ist zu Ende! Drücken Sie Abbrechen um alle Geschichten anzuzeigen!")
        }

        incrementStepCount()
        Log.d("Index danach", "${_stepCount.value}")
    }

    // Holt aus der List der Emotes die richtige raus
    fun getEmote(stepDto: StepDto): Int{

        var emoteName = stepDto.move!!.name.lowercase(Locale.GERMAN) + "_" + stepDto.durationInSeconds.toString()

        if(stepDto.move.name == "emote_hurra"){
            val rightEmote : EmoteDto? = emotes.filter{
                    e -> e.name == "hurra_" + stepDto.durationInSeconds.toString()
            }.firstOrNull()

            return rightEmote!!.path
        }

        val rightEmote : EmoteDto? = emotes.filter{
                e -> e.name == emoteName
        }.firstOrNull()

        Log.d("StepDto","${stepDto.move.name}")
        Log.d("Emote","${rightEmote!!.name}")

        if(rightEmote != null){
            return rightEmote.path;
        }
        return -1;
    }

    fun loadMmgDtos() {
        emotes = getEmotes()
        viewModelScope.launch {
            val result = HttpInstance.fetchMmgDtos()
            Log.d("Result:", "$result")

            if (result != null) {
                // val enabledMmgList = result.filter { it.enabled == true }
                _mmgList.value = result //enabledMmgList
                Log.d("Mmgs","${_mmgList.value}")
            }
            else{
                RoboterActions.speak("Ich habe keine Mitmachgeschichten gefunden")
            }
        }
    }

    fun emptyMmgList(){
        _mmgList.value = emptyList()
    }

    fun loadMmgSteps(id: Int){
        imageBitMap.value = null
        _mmgSteps.value = emptyList()
        viewModelScope.launch {
            val result = HttpInstance.fetchMmgSteps(id)
            Log.d("Steps","${result}")

            if(result != null){
                _mmgSteps.value = result

                if(_mmgSteps.value != null){
                    displayStep()
                }
            }
            else{
                RoboterActions.speak("Ich habe keine Informationen gefunden!")
            }
        }
    }

    fun base64ToBitmap(base64String: String): ImageBitmap? {
        //parts[0]= data:null;base64
        //parts[1] = base64String
        val parts = base64String.split(',');
        return try {
            val decodedBytes = Base64.decode(parts[1].trim(), Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(decodedBytes))
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}