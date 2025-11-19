package com.pepper.pep.mealplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.pepper.pep.mealplan.dto.Meal
import com.pepper.pep.mealplan.repository.MealPlanRepository
import com.pepper.pep.mealplan.ui.theme.PepmealplanTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), RobotLifecycleCallbacks {

    private val repository = MealPlanRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this)
        
        setContent {
            PepmealplanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MealPlanApp(repository)
                }
            }
        }
    }
    
    override fun onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this)
        super.onDestroy()
    }
    
    override fun onRobotFocusGained(qiContext: QiContext) {
        // The robot focus is gained.
        RoboterActions.qiContext = qiContext
        RoboterActions.robotExecute = true
        
        // Welcome message from Pepper
        RoboterActions.speak("Welcome to Pepper Meal Plan! Let me help you with your meals today.")
    }
    
    override fun onRobotFocusLost() {
        // The robot focus is lost.
        RoboterActions.robotExecute = false
    }
    
    override fun onRobotFocusRefused(reason: String) {
        // The robot focus is refused.
        RoboterActions.robotExecute = false
    }
}

@Composable
fun MealPlanApp(repository: MealPlanRepository) {
    var todaysMeals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            repository.getTodaysMeals().fold(
                onSuccess = { meals ->
                    todaysMeals = meals
                    isLoading = false
                    
                    // Pepper announces today's meals
                    if (meals.isNotEmpty()) {
                        RoboterActions.speak("Heute haben wir ${meals.size} Mahlzeiten geplannt!")
                    } else {
                        RoboterActions.speak("Keine Mahlzeiten sind heute verfügbar.")
                    }
                },
                onFailure = { error ->
                    errorMessage = error.message
                    isLoading = false
                    RoboterActions.speak("Keine Mahlzeiten sind heute verfügbar.")
                }
            )
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Pepper Meal Plan",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        when {
            isLoading -> {
                Text("Laden...")
            }
            errorMessage != null -> {
                Text("Error: $errorMessage")
            }
            todaysMeals.isEmpty() -> {
                Text("Keine Mahlzeiten heute verfügbar")
            }
            else -> {
                Text("Today's Meals:")
                todaysMeals.forEach { meal ->
                    Text(
                        text = "• ${meal.name} (${meal.mealType})",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}