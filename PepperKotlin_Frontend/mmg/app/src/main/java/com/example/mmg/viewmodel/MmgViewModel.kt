package com.example.mmg.viewmodel

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mmg.RoboterActions
import com.example.mmg.dto.MmgDto
import com.example.mmg.dto.StepDto
import com.example.mmg.network.HttpInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

class MmgViewModel : ViewModel() {
    private val _mmgList = MutableStateFlow<List<MmgDto>>(emptyList())
    val mmgList = _mmgList.asStateFlow()

    private val _mmgSteps = MutableStateFlow<List<StepDto>>(emptyList())
    val mmgSteps = _mmgSteps.asStateFlow()

    fun loadMmgDtos() {
        viewModelScope.launch {
            val result = HttpInstance.fetchMmgDtos()
            Log.d("Result:", "$result")

            if (result != null) {
                // val enabledMmgList = result.filter { it.enabled == true }
                _mmgList.value = result //enabledMmgList
            }
            else{
                //RoboterActions.speak("Ich habe keine Mitmachgeschichten gefunden")
            }
        }
    }

    fun loadMmgSteps(id: Int){
        viewModelScope.launch {
            val result = HttpInstance.fetchMmgSteps(id)
            Log.d("Steps","${result}")

            if(result != null){
                _mmgSteps.value = result
            }
            else{
                //RoboterActions.speak("Ich habe keine Informationen gefunden!")
            }
        }

    }

    fun base64ToBitmap(base64String: String): ImageBitmap? {
        //parts[0]= data:null;base64
        //parts[1] = base64String
        val parts = base64String.split(',');
        return try {
            val decodedBytes = Base64.decode(parts[1], Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(decodedBytes))
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}
