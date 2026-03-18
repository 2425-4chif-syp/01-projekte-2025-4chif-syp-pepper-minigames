package com.example.mmg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.example.mmg.common.Extras
import com.example.mmg.viewmodel.MmgViewModel
import androidx.navigation.compose.rememberNavController
import com.example.mmg.navigation.AppNavigaton
import com.example.mmg.session.InactivityLogoutManager

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {

    private lateinit var mmgViewModel: MmgViewModel
    private lateinit var inactivityLogoutManager: InactivityLogoutManager
    private var personIdFromMenu: Long = -1L
    private var personNameFromMenu: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inactivityLogoutManager = InactivityLogoutManager(this)
        updatePersonFromIntent(intent)
        QiSDK.register(this, this)

        setContent {
            mmgViewModel = viewModel()
            val navController = rememberNavController()
            AppNavigaton(
                navController = navController,
                mmgViewModel = mmgViewModel,
                onCloseApp = { closeAppAndReturnToMenu() }
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) return
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

    private fun updatePersonFromIntent(intent: Intent) {
        personIdFromMenu = intent.getLongExtra(Extras.PERSON_ID, -1L)
        personNameFromMenu = intent.getStringExtra(Extras.PERSON_NAME)?.trim()?.takeIf { it.isNotEmpty() }
    }

    private fun closeAppAndReturnToMenu() {
        val menuIntent = packageManager.getLaunchIntentForPackage(MENU_PACKAGE)
            ?: Intent().apply {
                setClassName(MENU_PACKAGE, MENU_MAIN_ACTIVITY)
            }

        menuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        if (personIdFromMenu > 0) {
            menuIntent.putExtra(Extras.PERSON_ID, personIdFromMenu)
        }
        personNameFromMenu?.let { menuIntent.putExtra(Extras.PERSON_NAME, it) }

        startActivity(menuIntent)
        finish()
    }

    private companion object {
        const val MENU_PACKAGE = "com.example.menu"
        const val MENU_MAIN_ACTIVITY = "com.example.menu.MainActivity"
    }
}
