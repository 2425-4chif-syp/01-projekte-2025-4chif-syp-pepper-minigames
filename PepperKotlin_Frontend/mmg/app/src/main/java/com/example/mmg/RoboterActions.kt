package com.example.mmg

import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.Future // <-- wichtig, das ist die richtige Future-Klasse von Pepper!

class RoboterActions {

    companion object {

        var qiContext: QiContext? = null
        var robotExecute: Boolean = false

        private var currentSayFuture: Future<Void>? = null

        fun speak(text: String): Future<Void>? {
            if (!robotExecute || qiContext == null) return null

            currentSayFuture?.let {
                if (!it.isDone) {
                    it.requestCancellation()
                }
            }

            return try {
                val say: Say = SayBuilder.with(qiContext)
                    .withText(text)
                    .build()
                currentSayFuture = say.async().run()
                currentSayFuture
            } catch (e: Exception) {
                Log.e("RoboterActions", "Fehler beim Sprechen: ${e.message}")
                null
            }
        }

        fun animation(resource: Int) {
            if (!robotExecute || qiContext == null) return
            try {
                val animation: Animation = AnimationBuilder.with(qiContext)
                    .withResources(resource)
                    .build()
                val animate: Animate = AnimateBuilder.with(qiContext)
                    .withAnimation(animation)
                    .build()
                animate.async().run()
            } catch (e: Exception) {
                Log.e("RoboterActions", "Fehler bei Animation: ${e.message}")
            }
        }
    }
}
