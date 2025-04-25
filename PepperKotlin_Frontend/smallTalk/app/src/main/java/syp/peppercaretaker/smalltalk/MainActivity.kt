package syp.peppercaretaker.smalltalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import syp.peppercaretaker.smalltalk.screen.SmallTalkScreen
import syp.peppercaretaker.smalltalk.viewmodel.SmallTalkViewModel

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this)
        setContent {
            SmallTalkScreen(viewModel = SmallTalkViewModel(this.application))
        }
    }
    override fun onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this)
        super.onDestroy()
    }
    override fun onRobotFocusGained(qiContext: QiContext) {
        // The robot focus is gained.
        PepperFuncs.qiContext = qiContext
        PepperFuncs.onPepper = true
    }
    override fun onRobotFocusLost() {
        // The robot focus is lost.
        QiSDK.unregister(this,this)
        super.onDestroy()
    }
    override fun onRobotFocusRefused(reason: String) {
        // The robot focus is refused.
    }
}