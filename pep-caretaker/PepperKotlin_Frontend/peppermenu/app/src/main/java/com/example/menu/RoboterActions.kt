package com.example.menu

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.camera.TakePicture
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.human.Human
import com.aldebaran.qi.sdk.`object`.humanawareness.HumanAwareness
import com.aldebaran.qi.sdk.`object`.image.EncodedImage
import com.aldebaran.qi.sdk.`object`.image.EncodedImageHandle
import com.aldebaran.qi.sdk.builder.ListenBuilder
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.builder.TakePictureBuilder
import java.nio.ByteBuffer
import java.util.concurrent.Future

class RoboterActions {

    companion object {

        // Kontext wird benötigt um die Verbindung mit dem Roboter zu gewährleisten
        var qiContext: QiContext? = null
        var robotExecute: Boolean = false


        fun speak(text: String): Future<Void>? {
            if (robotExecute) {
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

        fun takePicture(onImageCaptured: (ImageBitmap) -> Unit){
            if (robotExecute) {
                val takePictureFuture: com.aldebaran.qi.Future<TakePicture>? = TakePictureBuilder.with(qiContext).buildAsync()

                val timestampedImageHandleFuture = takePictureFuture?.andThenCompose { takePicture ->
                    Log.i(ContentValues.TAG, "Take picture launched!")
                    takePicture.async().run()
                }
                timestampedImageHandleFuture?.andThenConsume { timestampedImageHandle ->
                    Log.i(ContentValues.TAG, "Picture taken")
                    val encodedImageHandle: EncodedImageHandle = timestampedImageHandle.image
                    val encodedImage: EncodedImage = encodedImageHandle.value

                    val buffer: ByteBuffer = encodedImage.data
                    buffer.rewind()
                    val pictureArray = ByteArray(buffer.remaining())
                    buffer.get(pictureArray)

                    val pictureBitmap = BitmapFactory.decodeByteArray(pictureArray, 0, pictureArray.size)
                    val pictureImageBitmap = pictureBitmap.asImageBitmap()
                    Log.i(ContentValues.TAG, "Picture: ${pictureImageBitmap}")

                    onImageCaptured(pictureImageBitmap)
                }
            }
        }

        fun getHumanAwarness(): Human?{
            if(robotExecute){
                val humanAwareness: HumanAwareness = qiContext!!.humanAwareness
                return humanAwareness.engagedHuman
            }
            return null
        }

        fun waitForWakeWord(
            wakeWordOptions: List<String> = listOf("Hallo Pepper", "hallo pepper", "Hallo Peppa")
        ): Boolean {
            if (!robotExecute || qiContext == null || wakeWordOptions.isEmpty()) return false

            return try {
                val phraseSet = PhraseSetBuilder.with(qiContext)
                    .withTexts(*wakeWordOptions.toTypedArray())
                    .build()

                val listen = ListenBuilder.with(qiContext)
                    .withPhraseSet(phraseSet)
                    .build()

                val heardText = listen.run().heardPhrase.text.orEmpty()
                wakeWordOptions.any { option -> option.equals(heardText, ignoreCase = true) }
            } catch (e: Exception) {
                Log.e("PepperWakeWord", "Wake-word listening failed: ${e.message}")
                false
            }
        }
    }
}
