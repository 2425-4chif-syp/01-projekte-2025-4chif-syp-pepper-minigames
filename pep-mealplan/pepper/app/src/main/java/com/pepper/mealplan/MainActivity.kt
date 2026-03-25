package com.pepper.mealplan

import android.content.Intent
import android.os.Build
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
import com.pepper.mealplan.session.InactivityLogoutManager
import com.pepper.mealplan.ui.theme.MealplanTheme

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {
    private var initialPersonFromMenu by mutableStateOf<String?>(null)
    private var initialPersonIdFromMenu by mutableStateOf(-1L)
    private lateinit var inactivityLogoutManager: InactivityLogoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inactivityLogoutManager = InactivityLogoutManager(this)
        updatePersonFromIntent(intent)
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
                            initialPersonFromMenu = personFromMenu,
                            onCloseApp = { foundPerson ->
                                closeAppAndReturnToMenu(foundPerson)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        updatePersonFromIntent(intent)
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

    private fun extractPersonName(intent: Intent?): String? {
        return intent
            ?.getStringExtra(Extras.PERSON_NAME)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }

    private fun updatePersonFromIntent(intent: Intent?) {
        initialPersonFromMenu = extractPersonName(intent)
        initialPersonIdFromMenu = intent?.getLongExtra(Extras.PERSON_ID, -1L) ?: -1L
    }

    private fun closeAppAndReturnToMenu(foundPerson: String?) {
        val menuIntent = packageManager.getLaunchIntentForPackage(MENU_PACKAGE)
            ?: Intent().apply {
                setClassName(MENU_PACKAGE, MENU_MAIN_ACTIVITY)
            }

        menuIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        )
        if (initialPersonIdFromMenu > 0) {
            menuIntent.putExtra(Extras.PERSON_ID, initialPersonIdFromMenu)
        }

        val personName = foundPerson?.trim()?.takeIf { it.isNotEmpty() } ?: initialPersonFromMenu
        personName?.let { menuIntent.putExtra(Extras.PERSON_NAME, it) }

        startActivity(menuIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask()
        } else {
            finishAffinity()
        }
    }

    private companion object {
        const val MENU_PACKAGE = "com.example.menu"
        const val MENU_MAIN_ACTIVITY = "com.example.menu.MainActivity"
    }
}
