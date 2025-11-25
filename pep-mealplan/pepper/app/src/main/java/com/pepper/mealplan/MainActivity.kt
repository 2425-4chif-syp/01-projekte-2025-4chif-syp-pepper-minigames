package com.pepper.mealplan

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this)
        setContent{
            Column() {
                Text("Hello Pepper")
            }
        }
    }
    override fun onRobotFocusGained(qiContext: QiContext?) {
        RoboterActions.qiContext = qiContext
        RoboterActions.robotExecute = true
        Log.d("QiContext","${RoboterActions.robotExecute} / ${RoboterActions.qiContext}")
    }

    override fun onRobotFocusLost() {
        RoboterActions.robotExecute = false
        RoboterActions.qiContext = null
    }

    override fun onDestroy() {
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override fun onRobotFocusRefused(reason: String?) {
        // Robot focus was refused
    }
}