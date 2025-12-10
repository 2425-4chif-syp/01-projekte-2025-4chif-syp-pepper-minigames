package com.pepper.mealplan.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pepper.mealplan.MealPlanOverview.MealPlanOverview
import com.pepper.mealplan.createMealPlan.CreateMealPlan
import com.pepper.mealplan.faceRecognition.FaceRecognitionScreen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Overview : BottomNavItem("overview", Icons.Default.CalendarToday, "Wochenplanübersicht")
    object Create : BottomNavItem("create", Icons.Default.Add, "Mahlzeiten auswählen")
    object Logout : BottomNavItem("logout", Icons.Default.ExitToApp, "Abmelden")
}

@Composable
fun AppNavigation(navController: NavHostController){
    val items = listOf(
        BottomNavItem.Overview,
        BottomNavItem.Create,
        BottomNavItem.Logout
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Zeige BottomNavigation nur an, wenn wir nicht auf dem FaceRecognition Screen sind
    val showBottomNav = currentRoute != "face_recognition"
    
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primary
                ) {
                    items.forEach { item ->
                        BottomNavigationItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (item.route == "logout") {
                                    navController.navigate("face_recognition") {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                } else {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                }
                            },
                            selectedContentColor = MaterialTheme.colors.primary,
                            unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "face_recognition",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("face_recognition") { 
                FaceRecognitionScreen(
                    onAuthenticationSuccess = {
                        navController.navigate("overview") {
                            popUpTo("face_recognition") { inclusive = true }
                        }
                    }
                )
            }
            composable("overview") { MealPlanOverview() }
            composable("create") { CreateMealPlan() }
        }
    }
}