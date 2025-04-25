package syp.peppercaretaker.smalltalk

import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.builder.SayBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PepperFuncs{
    companion object {

        var qiContext: QiContext? = null

        var onPepper: Boolean = true

        fun speakAsync(text: String): Future<Void>? {
            if (onPepper) {
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

        fun speakSync(text: String) {
            if (onPepper) {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        val say = SayBuilder.with(qiContext)
                            .withText(text)
                            .build()
                        say.run()
                    }
                }
            }
        }
    }
}