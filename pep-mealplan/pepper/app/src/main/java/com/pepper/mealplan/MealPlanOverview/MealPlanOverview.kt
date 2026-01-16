package com.pepper.mealplan.MealPlanOverview

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.pepper.mealplan.R
import com.pepper.mealplan.RoboterActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MealPlanOverview(
    foundPerson: String = "",
    onGoToOrder: () -> Unit,
    viewModel: MealPlanOverviewViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return MealPlanOverviewViewModel(foundPerson) as T
            }
        }
    )
) {

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    val dayMeals = viewModel.threeDayMeals
    val initialMealIndexToday = viewModel.initialMealIndexToday

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colors.primary
                )
            }
        } else {
            if (dayMeals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kein Essensplan verfügbar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                val dayPagerState = rememberPagerState(initialPage = 0)
                val coroutineScope = rememberCoroutineScope()

                val currentDayIndex = dayPagerState.currentPage.coerceIn(0, dayMeals.lastIndex)
                val currentDay = dayMeals[currentDayIndex]

                // ---------- Kopfbereich mit Tag + Links/Rechts-Hinweisen ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Linker Pfeil / Hinweis
                    if (currentDayIndex > 0) {
                        val leftLabelText = when (currentDayIndex) {
                            1 -> "Heutigen Tag anzeigen"
                            2 -> "Morgen anzeigen"
                            else -> "${dayMeals[currentDayIndex - 1].label} anzeigen"
                        }

                        Column(
                            modifier = Modifier
                                .widthIn(min = 80.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        dayPagerState.animateScrollToPage(currentDayIndex - 1)
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⬅️",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = leftLabelText,
                                fontSize = 20.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(80.dp))
                    }

                    // Überschrift in der Mitte
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${currentDay.label} · ${formatDate(currentDay.calendar)}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Rechter Pfeil / Hinweis
                    if (currentDayIndex < dayMeals.lastIndex) {
                        val rightLabelText = when (currentDayIndex) {
                            0 -> "Morgen anzeigen"
                            1 -> "Übermorgen anzeigen"
                            else -> "${dayMeals[currentDayIndex + 1].label} anzeigen"
                        }

                        Column(
                            modifier = Modifier
                                .widthIn(min = 80.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        dayPagerState.animateScrollToPage(currentDayIndex + 1)
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "➡️",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = rightLabelText,
                                fontSize = 20.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(80.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ---------- Horizontaler Pager für Tage ----------
                HorizontalPager(
                    count = dayMeals.size,
                    state = dayPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { dayIndex ->
                    val day = dayMeals[dayIndex]
                    val initialPageForThisDay =
                        if (dayIndex == 0) mapMealIndexToGroupIndex(initialMealIndexToday) else 0

                    DayPager(
                        day = day,
                        initialPage = initialPageForThisDay
                    )
                }
            }
        }
    }
}

/**
 * Vertikaler Pager für einen Tag:
 *   Seite 0: Suppe
 *   Seite 1: Mittagessen (Hauptgericht 1 & 2)
 *   Seite 2: Dessert
 *   Seite 3: Abendessen (1 & 2)
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun DayPager(
    day: DayMealsUi,
    initialPage: Int
) {
    val pagerState = rememberPagerState(initialPage = initialPage.coerceIn(0, 3))
    val coroutineScope = rememberCoroutineScope()

    VerticalPager(
        count = 4,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->

        // Texte für Pepper pro Seite
        val speechTextMeals = remember(day, page) {
            buildSpeechTextForPage(day, page)
        }
        val speechTextNav = remember(page) {
            buildNavigationSpeechForPage(page)
        }

        val onReadMenu: () -> Unit = {
            coroutineScope.launch(Dispatchers.IO) {
                RoboterActions.speak(speechTextMeals)
            }
        }

        val onExplainNav: () -> Unit = {
            coroutineScope.launch(Dispatchers.IO) {
                RoboterActions.speak(speechTextNav)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (page) {
                    0 -> SoupScreen(day = day, onReadMenu = onReadMenu, onExplainNav = onExplainNav)
                    1 -> MiddayScreen(day = day, onReadMenu = onReadMenu, onExplainNav = onExplainNav)
                    2 -> DessertScreen(day = day, onReadMenu = onReadMenu, onExplainNav = onExplainNav)
                    3 -> DinnerScreen(day = day, onReadMenu = onReadMenu, onExplainNav = onExplainNav)
                }
            }

            val hintText = when (page) {
                0 -> "Wischen ⬇️ Mittagessen anzeigen"
                1 -> "Wischen ⬆️ Suppe           Wischen ⬇️ Dessert anzeigen"
                2 -> "Wischen ⬆️ Mittagessen             Wischen ⬇️ Abendessen anzeigen"
                else -> "Wischen ⬆️ Dessert anzeigen"
            }

            Text(
                text = hintText,
                fontSize = 20.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
            )
        }
    }
}

// ---------- Seite 0: Suppe ----------
@Composable
private fun SoupScreen(
    day: DayMealsUi,
    onReadMenu: () -> Unit,
    onExplainNav: () -> Unit
) {
    val soup = day.meals.getOrNull(0)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Suppe",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.fillMaxWidth()
        )

        soup?.let {
            MenuSection(
                title = it.title,
                foodName = it.foodName,
                backgroundColor = Color(0xFFE3F2FD),
                foodType = it.foodType,
                onReadMenu = onReadMenu,
                onExplainNav = onExplainNav,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

// ---------- Seite 1: Mittagessen (Hauptgericht 1 & 2) ----------
@Composable
private fun MiddayScreen(
    day: DayMealsUi,
    onReadMenu: () -> Unit,
    onExplainNav: () -> Unit
) {
    val main1 = day.meals.getOrNull(1)
    val main2 = day.meals.getOrNull(2)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mittagessen",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            main1?.let {
                MenuSection(
                    title = it.title,
                    foodName = it.foodName,
                    backgroundColor = Color(0xFFF3E5F5),
                    foodType = it.foodType,
                    onReadMenu = onReadMenu,
                    onExplainNav = onExplainNav,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        }
    }
}

// ---------- Seite 2: Dessert ----------
@Composable
private fun DessertScreen(
    day: DayMealsUi,
    onReadMenu: () -> Unit,
    onExplainNav: () -> Unit
) {
    val dessert = day.meals.getOrNull(3)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dessert",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.fillMaxWidth()
        )

        dessert?.let {
            MenuSection(
                title = it.title,
                foodName = it.foodName,
                backgroundColor = Color(0xFFFFF3E0),
                foodType = it.foodType,
                onReadMenu = onReadMenu,
                onExplainNav = onExplainNav,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

// ---------- Seite 3: Abendessen (1 & 2) ----------
@Composable
private fun DinnerScreen(
    day: DayMealsUi,
    onReadMenu: () -> Unit,
    onExplainNav: () -> Unit
) {
    val dinner1 = day.meals.getOrNull(4)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Abendessen",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            dinner1?.let {
                MenuSection(
                    title = it.title,
                    foodName = it.foodName,
                    backgroundColor = Color(0xFFE8F5E8),
                    foodType = it.foodType,
                    onReadMenu = onReadMenu,
                    onExplainNav = onExplainNav,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        }
    }
}

/**
 * Ordnet den Index der nächsten Mahlzeit (0..5) auf eine Seite zu:
 *  0       -> Seite 0 (Suppe)
 *  1,2     -> Seite 1 (Mittagessen)
 *  3       -> Seite 2 (Dessert)
 *  4,5     -> Seite 3 (Abendessen)
 */
private fun mapMealIndexToGroupIndex(mealIndex: Int): Int =
    when (mealIndex) {
        0 -> 0
        1, 2 -> 1
        3 -> 2
        4, 5 -> 3
        else -> 0
    }

// ---------------- MenuSection ----------------

@Composable
private fun MenuSection(
    title: String,
    foodName: String,
    backgroundColor: Color,
    foodType: String? = null,
    onReadMenu: (() -> Unit)? = null,
    onExplainNav: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 200.dp)   // höher als vorher
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bild links
            if (foodType != null) {
                val context = LocalContext.current
                val bitmap = remember(foodType) {
                    val imageRes = when (foodType) {
                        "soup" -> R.drawable.soup
                        "main" -> R.drawable.main
                        "dessert" -> R.drawable.dessert
                        else -> R.drawable.main
                    }

                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 4
                        inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
                    }

                    try {
                        val inputStream = context.resources.openRawResource(imageRes)
                        val bmp = BitmapFactory.decodeStream(inputStream, null, options)
                        inputStream.close()
                        bmp?.asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "$title Bild",
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (foodType) {
                                    "soup" -> Color(0xFF2196F3)
                                    "main" -> Color(0xFF4CAF50)
                                    "dessert" -> Color(0xFFFF9800)
                                    else -> Color.Gray
                                }
                            )
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
            }

            // Text in der Mitte
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = foodName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Rechts: Buttons untereinander im Kasten
            if (onReadMenu != null && onExplainNav != null) {
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.widthIn(min = 180.dp, max = 220.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onReadMenu,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_read_menu1),
                            contentDescription = "Menü vorlesen",
                            modifier = Modifier.size(90.dp)
                        )
                    }

                    Button(
                        onClick = onExplainNav,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_explain_nav1),
                            contentDescription = "Menü vorlesen",
                            modifier = Modifier.size(90.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(calendar: Calendar): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(calendar.time)
}

// ---------- Texte für Pepper ----------

private fun buildSpeechTextForPage(day: DayMealsUi, page: Int): String {
    val dateText = formatDate(day.calendar)

    return when (page) {
        0 -> {
            val soup = day.meals.getOrNull(0)?.foodName ?: "keine Angabe"
            "Am ${day.label}, dem $dateText, gibt es als Suppe: $soup."
        }
        1 -> {
            val m1 = day.meals.getOrNull(1)?.foodName ?: "keine Angabe"
            val m2 = day.meals.getOrNull(2)?.foodName ?: "keine Angabe"
            "Am ${day.label}, dem $dateText, gibt es zum Mittagessen: $m1 und $m2."
        }
        2 -> {
            val dessert = day.meals.getOrNull(3)?.foodName ?: "keine Angabe"
            "Am ${day.label}, dem $dateText, gibt es als Dessert: $dessert."
        }
        3 -> {
            val d1 = day.meals.getOrNull(4)?.foodName ?: "keine Angabe"
            val d2 = day.meals.getOrNull(5)?.foodName ?: "keine Angabe"
            "Am ${day.label}, dem $dateText, gibt es zum Abendessen: $d1 und $d2."
        }
        else -> "Ich habe für diese Seite keine Essensinformationen."
    }
}

private fun buildNavigationSpeechForPage(page: Int): String {
    return when (page) {
        0 -> "Du siehst jetzt die Suppe. Wische nach unten, um das Mittagessen anzuzeigen. Wische nach rechts oder links, um den Tag zu wechseln."
        1 -> "Du siehst jetzt das Mittagessen. Wische nach oben, um die Suppe zu sehen, oder nach unten, um das Dessert anzuzeigen. Nach rechts oder links wischen wechselt den Tag."
        2 -> "Du siehst jetzt das Dessert. Wische nach oben für das Mittagessen, oder nach unten für das Abendessen. Mit einer Wischbewegung nach rechts oder links wechselst du den Tag."
        3 -> "Du siehst jetzt das Abendessen. Wische nach oben, um das Dessert anzuzeigen. Nach rechts oder links wischen wechselt den Tag."
        else -> "Mit Wischen nach oben oder unten wechselst du die Mahlzeit. Mit Wischen nach rechts oder links wechselst du den Tag."
    }
}
