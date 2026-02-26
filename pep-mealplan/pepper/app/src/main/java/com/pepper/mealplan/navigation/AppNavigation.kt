package com.pepper.mealplan.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pepper.mealplan.data.order.MealOrderRepositoryProvider
import com.pepper.mealplan.features.create.CreateMealPlan
import com.pepper.mealplan.features.face.FaceRecognitionScreen
import com.pepper.mealplan.features.order.OrderReminderScreen
import com.pepper.mealplan.features.overview.MealPlanOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private fun enc(s: String) = Uri.encode(s)
private fun dec(s: String) = Uri.decode(s)

private object Routes {
    const val FACE = "face_recognition"
    const val OVERVIEW = "overview/{foundPerson}"
    const val CREATE = "create/{foundPerson}"
    const val REMINDER = "order_reminder/{foundPerson}"

    fun overview(person: String) = "overview/${enc(person)}"
    fun create(person: String) = "create/${enc(person)}"
    fun reminder(person: String) = "order_reminder/${enc(person)}"
}

sealed class BottomNavItem(val routeKey: String, val icon: ImageVector, val title: String) {
    object Overview : BottomNavItem("overview", Icons.Default.CalendarToday, "Wochenplanübersicht")
    object Create : BottomNavItem("create", Icons.Default.Add, "Mahlzeiten auswählen")
    object Logout : BottomNavItem("logout", Icons.Default.ExitToApp, "Abmelden")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val items = listOf(BottomNavItem.Overview, BottomNavItem.Create, BottomNavItem.Logout)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Aktive Person nur als State halten (nicht aus args schreiben!)
    var activePerson by remember { mutableStateOf("") }

    // Nach Face-Login wird hier gesetzt -> LaunchedEffect entscheidet Navigation
    var pendingAuthPerson by remember { mutableStateOf<String?>(null) }

    var missingCount by remember { mutableStateOf(0) }
    var showMissingDialog by remember { mutableStateOf(false) }

    val repo = remember { MealOrderRepositoryProvider.repository }

    // BottomNav nur zeigen, wenn nicht Face
    val showBottomNav = currentRoute != Routes.FACE

    // missingCount laden (nur Lunch/Dinner)
    LaunchedEffect(activePerson) {
        if (activePerson.isBlank()) {
            missingCount = 0
            return@LaunchedEffect
        }
        val missing = withContext(Dispatchers.IO) {
            repo.getMissingMealsForNextDays(activePerson, days = 3)
        }
        missingCount = missing.count { it.slot.name.startsWith("MAIN") || it.slot.name.startsWith("DINNER") }
    }

    // Nach Face Login: Missing prüfen und navigieren
    LaunchedEffect(pendingAuthPerson) {
        val person = pendingAuthPerson ?: return@LaunchedEffect

        val missing = withContext(Dispatchers.IO) {
            repo.getMissingMealsForNextDays(person, days = 3)
        }
        val hasMissing = missing.any { it.slot.name.startsWith("MAIN") || it.slot.name.startsWith("DINNER") }

        if (hasMissing) {
            navController.navigate(Routes.reminder(person)) {
                popUpTo(Routes.FACE) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate(Routes.overview(person)) {
                popUpTo(Routes.FACE) { inclusive = true }
                launchSingleTop = true
            }
        }

        pendingAuthPerson = null
    }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primary
                ) {
                    items.forEach { item ->
                        val isSelected = navBackStackEntry?.destination?.hierarchy?.any { dest ->
                            dest.route?.startsWith(item.routeKey) == true
                        } == true

                        BottomNavigationItem(
                            icon = {
                                if (item is BottomNavItem.Create && activePerson.isNotBlank()) {
                                    if (missingCount > 0) {
                                        Box {
                                            Icon(item.icon, contentDescription = item.title)
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 4.dp, y = (-4).dp)
                                                    .background(Color.Red, shape = CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("!", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        Icon(Icons.Default.Check, contentDescription = "Alles ok", tint = Color(0xFF2E7D32))
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.title)
                                }
                            },
                            label = {
                                if (item is BottomNavItem.Create && activePerson.isNotBlank()) {
                                    if (missingCount > 0) {
                                        Text(
                                            text = if (missingCount == 1) "1 fehlt" else "$missingCount fehlen",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Text("Alles ok", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Text(item.title)
                                }
                            },
                            selected = isSelected,
                            onClick = {
                                when (item) {
                                    is BottomNavItem.Logout -> {
                                        activePerson = ""
                                        pendingAuthPerson = null
                                        navController.navigate(Routes.FACE) {
                                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }

                                    is BottomNavItem.Overview -> {
                                        if (activePerson.isNotBlank()) {
                                            navController.navigate(Routes.overview(activePerson)) {
                                                popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                                                launchSingleTop = true
                                            }
                                        } else {
                                            navController.navigate(Routes.FACE) {
                                                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    }

                                    is BottomNavItem.Create -> {
                                        if (activePerson.isBlank()) {
                                            navController.navigate(Routes.FACE) {
                                                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        } else if (missingCount > 0) {
                                            showMissingDialog = true
                                        } else {
                                            navController.navigate(Routes.create(activePerson)) {
                                                popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.FACE,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.FACE) {
                FaceRecognitionScreen(
                    onAuthenticationSuccess = { person ->
                        activePerson = person
                        pendingAuthPerson = person
                    }
                )
            }

            composable(Routes.OVERVIEW) { backStackEntry ->
                val person = dec(backStackEntry.arguments?.getString("foundPerson") ?: "")
                activePerson = person
                MealPlanOverview(
                    foundPerson = person,
                    onGoToOrder = { navController.navigate(Routes.create(person)) { launchSingleTop = true } }
                )
            }

            composable(Routes.CREATE) { backStackEntry ->
                val person = dec(backStackEntry.arguments?.getString("foundPerson") ?: "")
                activePerson = person

                CreateMealPlan(
                    foundPerson = person,
                    onBackToMenu = {
                        navController.navigate(Routes.overview(person)) {
                            launchSingleTop = true
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                        }
                    }
                )
            }

            composable(Routes.REMINDER) { backStackEntry ->
                val person = dec(backStackEntry.arguments?.getString("foundPerson") ?: "")
                activePerson = person
                OrderReminderScreen(
                    foundPerson = person,
                    onGoToOrder = { navController.navigate(Routes.create(person)) { launchSingleTop = true } },
                    onShowMenu = { navController.navigate(Routes.overview(person)) { launchSingleTop = true } }
                )
            }
        }
    }

    if (showMissingDialog && activePerson.isNotBlank()) {
        val text =
            if (missingCount == 1) "Es fehlt noch 1 Bestellung in den nächsten Tagen."
            else "Es fehlen noch $missingCount Bestellungen in den nächsten Tagen."

        AlertDialog(
            onDismissRequest = { showMissingDialog = false },
            title = { Text("Fehlende Bestellungen", fontWeight = FontWeight.Bold) },
            text = { Text(text) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMissingDialog = false
                        navController.navigate(Routes.create(activePerson)) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                ) { Text("Jetzt bestellen") }
            },
            dismissButton = {
                TextButton(onClick = { showMissingDialog = false }) { Text("Später") }
            }
        )
    }
}