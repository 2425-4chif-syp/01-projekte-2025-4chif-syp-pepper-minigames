package com.pepper.mealplan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.pepper.mealplan.common.Extras
import com.pepper.mealplan.navigation.AppNavigation
import com.pepper.mealplan.ui.theme.MealplanTheme

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {
    private var initialPersonFromMenu by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialPersonFromMenu = extractPersonName(intent)
        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this)
        setContent{
            MealplanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val personFromMenu = initialPersonFromMenu
                    key(personFromMenu) {
                        val navController = androidx.navigation.compose.rememberNavController()
                        AppNavigation(
                            navController = navController,
                            initialPersonFromMenu = personFromMenu
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        initialPersonFromMenu = extractPersonName(intent)
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

    private fun extractPersonName(intent: Intent?): String? {
        return intent
            ?.getStringExtra(Extras.PERSON_NAME)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }
}
