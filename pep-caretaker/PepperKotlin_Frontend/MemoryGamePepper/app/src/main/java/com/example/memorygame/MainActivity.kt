package com.example.memorygame

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.memorygame.common.Extras
import com.example.memorygame.data.model.PersonIntent
import com.example.memorygame.data.remote.NetworkModule
import com.example.memorygame.data.remote.PersonApi
import com.example.memorygame.ui.menu.MainMenuScreen
import com.example.memorygame.ui.screens.*
import com.example.memorygame.ui.theme.MemoryGameTheme
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.memorygame.data.repository.IntentPersonProvider
import com.example.memorygame.session.InactivityLogoutManager
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var personApi: PersonApi
    private lateinit var inactivityLogoutManager: InactivityLogoutManager
    private var personFromIntent by mutableStateOf<PersonIntent?>(null)
    private var personId by mutableStateOf(-1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inactivityLogoutManager = InactivityLogoutManager(this, timeoutMs = 90_000L)

        // Initialisiere TextToSpeech
        textToSpeech = TextToSpeech(this, this)

        // Per Intent die person holen und einstezten
        personApi = NetworkModule.providePersonApi()
        updatePersonFromIntent(intent)


        setContent {
            MemoryGameTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main_menu") {
                    // Hauptmenü
                    composable("main_menu") {
                        MainMenuScreen(
                            navController = navController,
                            personIntent = personFromIntent,
                            onCloseApp = { closeAppAndReturnToMenu() }
                        )
                    }

                    // Grid-Auswahl
                    composable("grid_selection") {
                        GridSelectionScreen(navController)
                    }

                    // Spiel
                    composable(
                        route = "game_screen/{rows}/{columns}",
                        arguments = listOf(
                            navArgument("rows") { type = NavType.IntType },
                            navArgument("columns") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val rows = backStackEntry.arguments?.getInt("rows") ?: 4
                        val columns = backStackEntry.arguments?.getInt("columns") ?: 4
                        MemoryGameScreen(
                            navController = navController,
                            rows = rows,
                            columns = columns,
                            personIntent = personFromIntent,
                            personId = personId,
                            personApi = personApi
                        )

                    }

                    composable("high_scores") {
                        HighScoresScreen(
                            currentPlayerId = personId,
                            navController = navController
                        )
                    }

                    // Spieleinleitung
                    composable("instructions") {
                        InstructionsScreen(
                            textToSpeech = textToSpeech,
                            navController = navController
                        )
                    }
                }
            }
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

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.GERMAN
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) return

        setIntent(intent)
        updatePersonFromIntent(intent)
        inactivityLogoutManager.onUserInteraction()
    }

    override fun onDestroy() {
        textToSpeech.shutdown()
        super.onDestroy()
    }

    private fun updatePersonFromIntent(intent: Intent) {
        val resolvedPerson: PersonIntent? = runBlocking {
            IntentPersonProvider(intent, personApi).getPerson()
        }

        personFromIntent = resolvedPerson
        personId = resolvedPerson?.id ?: intent.getLongExtra(Extras.PERSON_ID, -1L)
    }

    private fun closeAppAndReturnToMenu() {
        val menuIntent = packageManager.getLaunchIntentForPackage(MENU_PACKAGE)
            ?: Intent().apply {
                setClassName(MENU_PACKAGE, MENU_MAIN_ACTIVITY)
            }

        menuIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        )
        if (personId > 0) {
            menuIntent.putExtra(Extras.PERSON_ID, personId)
        }

        val personName = personFromIntent?.let { "${it.firstName} ${it.lastName}".trim() }
            ?: intent.getStringExtra(Extras.PERSON_NAME)?.trim()?.takeIf { it.isNotEmpty() }
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
