package com.pepper.mealplan.features.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pepper.mealplan.PepperPhrases
import com.pepper.mealplan.RoboterActions
import com.pepper.mealplan.data.order.MealOrderRepositoryProvider
import com.pepper.mealplan.data.order.MealSlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

private const val LUNCH_TIME_MINUTES = 12 * 60
private const val DINNER_TIME_MINUTES = 17 * 60 + 30

@Composable
fun OrderReminderScreen(
    foundPerson: String,
    onGoToOrder: () -> Unit,
    onShowMenu: () -> Unit
) {
    val repository = MealOrderRepositoryProvider.repository
    val missingMeals = remember(foundPerson) {
        repository.getMissingMealsForNextDays(foundPerson, days = 3)
    }

    val namePart = if (foundPerson.isNotBlank()) "$foundPerson, " else ""

    // Gruppiert pro Datum: Mittag fehlt? Abend fehlt?
    val missingByDay = remember(missingMeals) {
        val map = linkedMapOf<String, Pair<Boolean, Boolean>>() // dateKey -> (lunchMissing, dinnerMissing)

        missingMeals.forEach { info ->
            val existing = map[info.dateKey] ?: (false to false)
            val lunchMissing = existing.first || info.slot == MealSlot.MAIN1 || info.slot == MealSlot.MAIN2
            val dinnerMissing = existing.second || info.slot == MealSlot.DINNER1 || info.slot == MealSlot.DINNER2
            map[info.dateKey] = lunchMissing to dinnerMissing
        }

        // dateKey ist yyyy-MM-dd -> Sortierung klappt lexikografisch
        map.entries.sortedBy { it.key }.mapNotNull { entry ->
            val (effectiveLunchMissing, effectiveDinnerMissing) =
                applyTodayWindow(entry.key, entry.value.first, entry.value.second)

            if (!effectiveLunchMissing && !effectiveDinnerMissing) {
                null
            } else {
                DayMissing(
                    dateKey = entry.key,
                    lunchMissing = effectiveLunchMissing,
                    dinnerMissing = effectiveDinnerMissing
                )
            }
        }
    }

    val summaryText = remember(missingByDay) {
        val missingCount = missingByDay.sumOf { day ->
            (if (day.lunchMissing) 1 else 0) + (if (day.dinnerMissing) 1 else 0)
        }
        if (missingByDay.isEmpty()) {
            "Sehr gut. Es ist aktuell keine Bestellung mehr offen."
        } else if (missingCount == 1) {
            "Es fehlt noch 1 Bestellung."
        } else {
            "Es fehlen noch $missingCount Bestellungen."
        }
    }

    // ---------- Text für Pepper ----------
    val speechText = remember(missingByDay) {
        val baseText = if (missingByDay.isEmpty()) {
            namePart + "alle Mahlzeiten der nächsten drei Tage sind bereits bestellt."
        } else if (missingByDay.size == 1) {
            val first = missingByDay.first()
            val weekDay = formatWeekday(first.dateKey)
            val whatMissing = missingMealsText(first.lunchMissing, first.dinnerMissing)

            namePart +
                    "für $weekDay, hast du $whatMissing noch nicht bestellt. " +
                    "Drücke auf den blauen Button, um mit der Bestellung fortzufahren. " +
                    "Wenn du nur den Essensplan ansehen möchtest, drücke auf den grünen Button."
        } else {
            val header =
                namePart + "für die nächsten Tage hast du noch nicht alle Mahlzeiten bestellt: "

            val listText = missingByDay.joinToString(separator = " ") { day ->
                val weekDay = formatWeekday(day.dateKey)
                val whatMissing = missingMealsText(day.lunchMissing, day.dinnerMissing)
                "am $weekDay, $whatMissing."
            }

            header + listText +
                    " Drücke auf den blauen Button, um mit der Bestellung fortzufahren. " +
                    "Wenn du nur den Essensplan ansehen möchtest, drücke auf den grünen Button."
        }
        PepperPhrases.reminderSpeech(baseText)
    }

    // ---------- Pepper sprechen ----------
    LaunchedEffect(speechText) {
        if (speechText.isNotBlank()) {
            withContext(Dispatchers.IO) {
                RoboterActions.stopSpeaking()
                RoboterActions.speak(speechText)
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF5FAFF), Color(0xFFEAF6F1))
                )
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Bestell-Erinnerung",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
        )

        Text(
            text = if (foundPerson.isNotBlank()) "Hallo $foundPerson" else "Hallo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 6.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(14.dp),
            backgroundColor = Color.White,
            elevation = 4.dp
        ) {
            Text(
                text = summaryText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1B1B),
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (missingByDay.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    backgroundColor = Color(0xFFE8F5E9),
                    elevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Alles erledigt.\nDu kannst den Essensplan ansehen.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1B5E20)
                    )
                }
            } else {
                missingByDay.forEach { day ->
                    ReminderDayCard(day = day)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    RoboterActions.stopSpeaking()
                    onGoToOrder()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Bestellung starten",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    RoboterActions.stopSpeaking()
                    onShowMenu()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF2E7D32)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Nur Essensplan anzeigen",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ReminderDayCard(day: DayMissing) {
    val weekDay = formatWeekday(day.dateKey)
    val niceDate = formatDateKey(day.dateKey)

    Card(
        shape = RoundedCornerShape(14.dp),
        backgroundColor = Color.White,
        elevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$weekDay, $niceDate",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF102A43)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (day.lunchMissing) {
                    MissingBadge(text = "Mittagessen fehlt", color = Color(0xFFFFF3E0), textColor = Color(0xFF8A3E00))
                }
                if (day.dinnerMissing) {
                    MissingBadge(text = "Abendessen fehlt", color = Color(0xFFFFEBEE), textColor = Color(0xFF9B1B1B))
                }
            }
        }
    }
}

@Composable
private fun MissingBadge(
    text: String,
    color: Color,
    textColor: Color
) {
    Card(
        backgroundColor = color,
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

private data class DayMissing(
    val dateKey: String,
    val lunchMissing: Boolean,
    val dinnerMissing: Boolean
)        

// Textbaustein: was fehlt an diesem Tag?
private fun missingMealsText(lunchMissing: Boolean, dinnerMissing: Boolean): String {
    return when {
        lunchMissing && dinnerMissing -> "das Mittagessen und das Abendessen"
        lunchMissing -> "das Mittagessen"
        dinnerMissing -> "das Abendessen"
        else -> "keine Mahlzeit"
    }
}

private fun formatWeekday(dateKey: String): String {
    return try {
        val parts = dateKey.split("-")
        if (parts.size == 3) {
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1
            val day = parts[2].toInt()

            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
            }

            cal.getDisplayName(
                Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.GERMAN
            ) ?: ""
        } else ""
    } catch (e: Exception) {
        ""
    }
}

private fun formatDateKey(dateKey: String): String {
    return try {
        val parts = dateKey.split("-")
        if (parts.size == 3) {
            "${parts[2]}.${parts[1]}.${parts[0]}"
        } else dateKey
    } catch (e: Exception) {
        dateKey
    }
}

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
