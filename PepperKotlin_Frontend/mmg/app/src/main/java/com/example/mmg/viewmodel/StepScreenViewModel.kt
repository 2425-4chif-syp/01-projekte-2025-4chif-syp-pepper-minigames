package com.example.mmg.viewmodel

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import java.io.ByteArrayInputStream

class StepScreenViewModel: ViewModel(){

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