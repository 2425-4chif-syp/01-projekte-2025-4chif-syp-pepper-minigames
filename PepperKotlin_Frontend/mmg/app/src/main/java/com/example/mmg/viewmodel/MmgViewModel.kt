package com.example.mmg.viewmodel

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mmg.RoboterActions
import com.example.mmg.dto.EmoteDto
import com.example.mmg.dto.MmgDto
import com.example.mmg.dto.StepDto
import com.example.mmg.dto.getEmotes
import com.example.mmg.network.HttpInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

        incrementStepCount()

        if(_stepCount.value == _mmgSteps.value.size){
            stepsFinished.value = true
            RoboterActions.speak("Die Geschichte ist zu Ende!")
        }
    }

    // Holt aus der List der Emotes die richtige raus
    fun getEmote(stepDto: StepDto): Int{

        var emoteName = ""

        if(stepDto.durationInSeconds == 0){
            emoteName = stepDto.move!!.name.lowercase(Locale.GERMAN) + "_" + "5"
        }
        else{
            emoteName = stepDto.move!!.name.lowercase(Locale.GERMAN) + "_" + stepDto.durationInSeconds.toString()
        }

        val rightEmote : EmoteDto? = emotes.filter{
                e -> e.name == emoteName
        }.firstOrNull()

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
                _mmgList.value = result
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
        val parts = base64String.split(',')
        return try {
            val decodedBytes = Base64.decode(parts[1].trim(), Base64.DEFAULT)

            val options = BitmapFactory.Options()

            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(ByteArrayInputStream(decodedBytes), null, options)

            options.inSampleSize = calculateInSampleSize(options, 800, 600)

            options.inJustDecodeBounds = false
            options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
            
            val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(decodedBytes), null, options)
            bitmap?.asImageBitmap()
        } catch (e: OutOfMemoryError) {
            Log.e("MmgViewModel", "OutOfMemoryError when decoding base64 image", e)
            null
        } catch (e: Exception) {
            Log.e("MmgViewModel", "Error decoding base64 image", e)
            null
        }
    }
    
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}