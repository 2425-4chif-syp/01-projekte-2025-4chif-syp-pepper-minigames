package com.pepper.mealplan.faceRecognition

import androidx.lifecycle.ViewModel
import com.pepper.mealplan.RoboterActions

class FaceRecognitionViewModel : ViewModel(){
    fun talkToPerson(){
        if(RoboterActions.getHumanAwarness() != null){
            RoboterActions.speak("Haben Sie schon Ihre Mahlzeiten eingetragen?")
        }
    }

    fun takePicture(){
        RoboterActions.takePicture {
            // Request
        }
    }
}