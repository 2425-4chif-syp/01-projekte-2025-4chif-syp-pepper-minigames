package com.example.menu

import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.builder.SayBuilder
import java.util.concurrent.Future

class RoboterActions {

    companion object {

        // Kontext wird benötigt um die Verbindung mit dem Roboter zu gewährleisten
        var qiContext: QiContext? = null
        var robotExecute: Boolean = true


        fun speak(text: String): Future<Void>?{
            if(robotExecute){
                val say: Future<Say>? = SayBuilder.with(qiContext)
                    .withText(text)
                    .buildAsync()

                while (!say!!.isDone) {
                }

                return try {
                    say.get().async().run()
                } catch (e: Exception) {
                    null
                }
            }
            return null;
            }
        }
    }
}