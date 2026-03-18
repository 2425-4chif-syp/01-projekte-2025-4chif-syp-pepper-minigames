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
import com.example.mmg.session.InactivityLogoutManager

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {

    private lateinit var mmgViewModel: MmgViewModel
    private lateinit var inactivityLogoutManager: InactivityLogoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inactivityLogoutManager = InactivityLogoutManager(this)
        QiSDK.register(this, this)

        setContent {
            mmgViewModel = viewModel()
            val navController = rememberNavController()
            AppNavigaton(navController = navController, mmgViewModel = mmgViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        inactivityLogoutManager.onResume()
    }

    override fun onPause() {
        inactivityLogoutManager.onPause()
        super.onPause()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        inactivityLogoutManager.onUserInteraction()
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
