package com.example.menu

import MainMenuScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.menu.presentation.LoginScreen
import com.example.menu.ui.theme.MenuTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)

        setContent {
            MenuTheme {
                // Initialisiere NavController
                val navController = rememberNavController()

                // Setze den ViewModelStore
                navController.setViewModelStore(viewModelStore)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // Setze den NavHost, nachdem ViewModelStore gesetzt wurde
                    NavHost(navController = navController, startDestination = "main_menu") {
                        composable("main_menu") {
                            MainMenuScreen(navController = navController)
                        }
                        composable("login_screen") {
                            LoginScreen(
                                onLoginClick = {
                                    // Nach erfolgreichem Login, zum Hauptmenü weiterleiten
                                    navController.navigate("main_menu")
                                },
                                onContinueWithoutLogin = {
                                    // Weiter ohne Anmeldung
                                    navController.navigate("main_menu")
                                },
                                navController = navController // NavController übergeben
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        TODO("Not yet implemented")
    }

    override fun onRobotFocusLost() {
        TODO("Not yet implemented")
    }

    override fun onRobotFocusRefused(reason: String?) {
        TODO("Not yet implemented")
    }
}