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
import com.pepper.mealplan.data.order.MealSlot
import com.pepper.mealplan.data.order.MissingMealInfo
import com.pepper.mealplan.data.order.MealOrderRepositoryProvider
import com.pepper.mealplan.features.create.CreateMealPlan
import com.pepper.mealplan.features.face.FaceRecognitionScreen
import com.pepper.mealplan.features.order.OrderReminderScreen
import com.pepper.mealplan.features.overview.MealPlanOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

private fun enc(s: String) = Uri.encode(s)
private fun dec(s: String) = Uri.decode(s)

private const val LUNCH_TIME_MINUTES = 12 * 60
private const val DINNER_TIME_MINUTES = 17 * 60 + 30

private fun currentDateKey(): String {
    val cal = Calendar.getInstance()
    return String.format(
        Locale.US, "%04d-%02d-%02d",
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH)
    )
}

private fun currentMinutes(): Int {
    val cal = Calendar.getInstance()
    return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
}

private fun applyTodayWindow(
    dateKey: String,
    lunchMissing: Boolean,
    dinnerMissing: Boolean
): Pair<Boolean, Boolean> {
    if (dateKey != currentDateKey()) return lunchMissing to dinnerMissing

    val now = currentMinutes()
    return when {
        now >= DINNER_TIME_MINUTES -> false to false
        now >= LUNCH_TIME_MINUTES -> false to dinnerMissing
        lunchMissing -> true to false
        else -> false to dinnerMissing
    }
}

private fun effectiveMissingCount(missing: List<MissingMealInfo>): Int {
    val byDate = missing.groupBy { it.dateKey }
    var count = 0

    byDate.forEach { (dateKey, entries) ->
        val lunchMissing = entries.any { it.slot == MealSlot.MAIN1 || it.slot == MealSlot.MAIN2 }
        val dinnerMissing = entries.any { it.slot == MealSlot.DINNER1 || it.slot == MealSlot.DINNER2 }
        val (effectiveLunchMissing, effectiveDinnerMissing) =
            applyTodayWindow(dateKey, lunchMissing, dinnerMissing)

        if (effectiveLunchMissing) count++
        if (effectiveDinnerMissing) count++
    }

    return count
}

private object Routes {
    const val BOOTSTRAP = "bootstrap_auth"
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
fun AppNavigation(
    navController: NavHostController,
    initialPersonFromMenu: String? = null
) {
    val items = listOf(BottomNavItem.Overview, BottomNavItem.Create, BottomNavItem.Logout)
    val bottomNavHeight = 48.dp
    val bottomNavIconSize = 20.dp
    val bottomNavLabelSize = 11.sp

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val normalizedInitialPerson = initialPersonFromMenu
        ?.trim()
        ?.replace(Regex("\\s+"), " ")
        ?.takeIf { it.isNotBlank() }

    // Aktive Person nur als State halten (nicht aus args schreiben!)
    var activePerson by remember { mutableStateOf(normalizedInitialPerson.orEmpty()) }

    // Nach Face-Login wird hier gesetzt -> LaunchedEffect entscheidet Navigation
    var pendingAuthPerson by remember { mutableStateOf<String?>(normalizedInitialPerson) }

    var missingCount by remember { mutableStateOf(0) }
    var showMissingDialog by remember { mutableStateOf(false) }
    var missingCountRefreshTick by remember { mutableStateOf(0) }

    val repo = remember { MealOrderRepositoryProvider.repository }

    // BottomNav nur zeigen, wenn nicht Face
    val showBottomNav = currentRoute != Routes.FACE && currentRoute != Routes.BOOTSTRAP

    // missingCount laden (nur Lunch/Dinner)
    LaunchedEffect(activePerson, missingCountRefreshTick) {
        if (activePerson.isBlank()) {
            missingCount = 0
        } else {
            val missing = withContext(Dispatchers.IO) {
                repo.getMissingMealsForNextDays(activePerson, days = 3)
            }
            missingCount = effectiveMissingCount(missing)
        }
    }

    // Nach Face Login: Missing prüfen und navigieren
    LaunchedEffect(pendingAuthPerson) {
        val person = pendingAuthPerson ?: return@LaunchedEffect

        val missing = withContext(Dispatchers.IO) {
            repo.getMissingMealsForNextDays(person, days = 3)
        }
        val hasMissing = effectiveMissingCount(missing) > 0

        if (hasMissing) {
            navController.navigate(Routes.reminder(person)) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate(Routes.overview(person)) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }

        pendingAuthPerson = null
    }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigation(
                    modifier = Modifier.height(bottomNavHeight),
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
                                            Icon(
                                                item.icon,
                                                contentDescription = item.title,
                                                modifier = Modifier.size(bottomNavIconSize)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 4.dp, y = (-4).dp)
                                                    .background(Color.Red, shape = CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("!", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Alles Bestellt",
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(bottomNavIconSize)
                                        )
                                    }
                                } else {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(bottomNavIconSize)
                                    )
                                }
                            },
                            label = {
                                if (item is BottomNavItem.Create && activePerson.isNotBlank()) {
                                    if (missingCount > 0) {
                                        Text(
                                            text = if (missingCount == 1) "1 Bestellung fehlt" else "$missingCount Bestellungen fehlen",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = bottomNavLabelSize
                                        )
                                    } else {
                                        Text(
                                            "Alles bestellt",
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = bottomNavLabelSize
                                        )
                                    }
                                } else {
                                    Text(item.title, fontSize = bottomNavLabelSize, fontWeight = FontWeight.SemiBold)
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
            startDestination = if (normalizedInitialPerson == null) Routes.FACE else Routes.BOOTSTRAP,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.BOOTSTRAP) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Lade Anmeldung...", fontWeight = FontWeight.Bold)
                }
            }

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
                    onOrderSuccess = {
                        missingCountRefreshTick++
                    },
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
