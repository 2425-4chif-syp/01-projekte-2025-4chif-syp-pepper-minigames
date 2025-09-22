package com.example.mmg

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.example.mmg.viewmodel.MmgViewModel
import androidx.navigation.compose.rememberNavController
import com.example.mmg.navigation.AppNavigaton

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {

    private lateinit var mmgViewModel: MmgViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)

        setContent {
            mmgViewModel = viewModel()

            val navController = rememberNavController()
            AppNavigaton(navController = navController, mmgViewModel = mmgViewModel)
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        RoboterActions.qiContext = qiContext
        Log.d("QiContext:", "Focus: ${RoboterActions.qiContext}")
        RoboterActions.robotExecute = true
    }

    override fun onRobotFocusLost() {
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override fun onRobotFocusRefused(reason: String?) {
        reason?.let { Log.d("Reason:", it) }
    }
}