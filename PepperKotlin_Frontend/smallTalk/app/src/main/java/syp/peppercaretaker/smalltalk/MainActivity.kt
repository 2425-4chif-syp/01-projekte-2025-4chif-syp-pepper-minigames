package syp.peppercaretaker.smalltalk

import android.os.Bundle
import android.util.Log
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
        QiSDK.register(this, this)
        setContent {
            SmallTalkScreen(viewModel = SmallTalkViewModel(this.application))
        }
    }
    override fun onRobotFocusGained(qiContext: QiContext?) {
        // Context für Pepper um seine Funktionen aufrufen zu können
        PepperFuncs.qiContext = qiContext
        Log.d("QiContext:", "Focus: ${PepperFuncs.qiContext}")
        // robotExecute gibt an, ob die Roboter Funktionen beim Aufrufen ausgeführt werden sollen
        PepperFuncs.onPepper = true
    }

    override fun onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override fun onRobotFocusLost() {
        // The robot focus is lost.
        QiSDK.unregister(this,this)
        super.onDestroy()
    }

    override fun onRobotFocusRefused(reason: String?) {

        // Im Logcat werden Fehlermeldungen ausgeben, falls die Verbindung unterbrochen wird
        if(reason != null){
            Log.d("Reason:",reason)
        }
    }
}