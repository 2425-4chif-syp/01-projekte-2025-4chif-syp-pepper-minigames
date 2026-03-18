package com.example.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.example.menu.common.Extras
import com.example.menu.common.Packages
import com.example.menu.dto.Person
import com.example.menu.presentation.InitialFaceRecognitionScreen
import com.example.menu.presentation.LoginScreen
import com.example.menu.screens.MainMenuScreen
import com.example.menu.ui.theme.MenuTheme
import com.example.menu.viewmodel.LoginScreenViewModel

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)

        setContent {
            MenuTheme {
                val navController = rememberNavController()
                navController.setViewModelStore(viewModelStore)
                var authenticatedPerson by remember { mutableStateOf<Person?>(null) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavHost(navController = navController, startDestination = "initial_face_login") {
                        composable("initial_face_login") {
                            InitialFaceRecognitionScreen(
                                onAuthenticationSuccess = { person ->
                                    authenticatedPerson = person
                                    navController.navigate("main_menu") {
                                        popUpTo("initial_face_login") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onManualSelectionRequired = {
                                    navController.navigate("manual_name_login") {
                                        popUpTo("initial_face_login") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable("manual_name_login") {
                            val vm: LoginScreenViewModel = viewModel()
                            LoginScreen(
                                onLoginClick = { person ->
                                    authenticatedPerson = person
                                    navController.navigate("main_menu") {
                                        popUpTo("manual_name_login") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                viewModel = vm
                            )
                        }

                        composable("main_menu") {
                            MainMenuScreen(
                                personName = authenticatedPerson?.let { "${it.firstName} ${it.lastName}".trim() },
                                onOpenApp = { packageName ->
                                    launchExternalApp(packageName, authenticatedPerson)
                                }
                            )
                        }
                    }
                }
            }
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
        if (reason != null) {
            Log.d("Reason:", reason)
        }
    }

    private fun launchExternalApp(packageName: String, person: Person?) {
        val requiresIdentifiedPerson =
            packageName == Packages.MEMORY_GAME || packageName == Packages.ESSENSPLAN

        if (requiresIdentifiedPerson && person == null) {
            Toast.makeText(this, "Bitte zuerst eine Person anmelden", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            person?.let {
                putExtra(Extras.PERSON_ID, it.pid)
                putExtra(Extras.PERSON_NAME, "${it.firstName} ${it.lastName}".trim())
            }
        }

        if (intent != null) {
            Log.d("PepperMenu", "Launching pkg=$packageName with person=${person?.pid}")
            startActivity(intent)
        } else {
            Toast.makeText(this, "App wurde noch nicht installiert", Toast.LENGTH_SHORT).show()
            Log.e("PepperMenu", "App mit Package $packageName nicht gefunden")
        }
    }
}
