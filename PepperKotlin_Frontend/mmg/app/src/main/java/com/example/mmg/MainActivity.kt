package com.example.mmg

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.example.mmg.presentation.MmgScreen
import com.example.mmg.viewmodel.MmgViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mmg.navigation.AppNavigaton

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)
        setContent {
            val navController = rememberNavController()
            AppNavigaton(navController = navController)
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        // Context für Pepper um seine Funktionen aufrufen zu können
        RoboterActions.qiContext = qiContext
        Log.d("QiContext:", "Focus: ${RoboterActions.qiContext}")
        // robotExecute gibt an, ob die Roboter Funktionen beim Aufrufen ausgeführt werden sollen
        RoboterActions.robotExecute = false
    }

    override fun onRobotFocusLost() {
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