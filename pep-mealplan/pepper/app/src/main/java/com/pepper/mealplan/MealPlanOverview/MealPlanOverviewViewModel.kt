package com.pepper.mealplan.MealPlanOverview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepper.mealplan.data.DataInserts
import com.pepper.mealplan.network.dto.FoodDto
import com.pepper.mealplan.network.dto.MenuDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

// Eine Mahlzeit (Suppe, Hauptgericht, Dessert, Abendessen)
data class MealItem(
    val title: String,
    val foodName: String,
    val foodType: String,
    val timeMinutes: Int // Minuten seit Mitternacht
)

// UI-Daten für einen Tag (Datum + alle Mahlzeiten)
data class DayMealsUi(
    val calendar: Calendar,
    val label: String,       // "Heute", "Morgen", "Übermorgen"
    val weekdayCode: String, // "MO", "DI", ...
    val menu: MenuDto?,
    val meals: List<MealItem>
)

class MealPlanOverviewViewModel(
    private val foundPerson: String = ""
) : ViewModel() {

    var currentWeek by mutableStateOf(1)
        private set

    var isLoading by mutableStateOf(true)
        private set

    // Getter für foundPerson falls andere Komponenten darauf zugreifen müssen
    val personName: String get() = foundPerson

    private val allData by lazy { DataInserts.getAllData() }

    private val foodsMap by lazy {
        allData.foods.associateBy { it.id }
    }

    // Heute + 2 Tage
    var threeDayMeals by mutableStateOf<List<DayMealsUi>>(emptyList())
        private set

    // Index der nächsten Mahlzeit für heute
    var initialMealIndexToday by mutableStateOf(0)
        private set

    init {
        refreshData()
    }

    fun getFoodById(id: Int?): FoodDto? {
        return if (id != null) foodsMap[id] else null
    }

    fun refreshData() {
        viewModelScope.launch {
            isLoading = true

            val todayCal = Calendar.getInstance()

            // WeekNumber für heute (6-Wochen-Zyklus)
            currentWeek = getWeekNumberForDate(todayCal)

            // Heute + 2 Tage aufbauen
            val days = (0..2).map { offset ->
                val cal = (todayCal.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, offset)
                }
                val indexFromToday = offset
                val weekdayCode = cal.toWeekdayCode()
                val weekNumberForDate = getWeekNumberForDate(cal)

                val menu = allData.menus.firstOrNull {
                    it.weekNumber == weekNumberForDate && it.weekday == weekdayCode
                }

                buildDayMealsUi(
                    calendar = cal,
                    indexFromToday = indexFromToday,
                    weekdayCode = weekdayCode,
                    menu = menu
                )
            }

            threeDayMeals = days

            // nächste Mahlzeit für heute
            val nowMinutes = todayCal.get(Calendar.HOUR_OF_DAY) * 60 +
                    todayCal.get(Calendar.MINUTE)

            val todayUi = threeDayMeals.firstOrNull()
            initialMealIndexToday = calculateInitialMealIndex(
                nowMinutes = nowMinutes,
                day = todayUi
            )

            delay(300)
            isLoading = false
        }
    }

    // ---------------- Hilfsfunktionen ----------------

    private fun buildDayMealsUi(
        calendar: Calendar,
        indexFromToday: Int,
        weekdayCode: String,
        menu: MenuDto?
    ): DayMealsUi {
        val label = when (indexFromToday) {
            0 -> "Heute"
            1 -> "Morgen"
            2 -> "Übermorgen"
            else -> getGermanWeekdayFromCode(weekdayCode)
        }

        if (menu == null) {
            return DayMealsUi(
                calendar = calendar,
                label = label,
                weekdayCode = weekdayCode,
                menu = null,
                meals = emptyList()
            )
        }

        fun foodName(id: Int?): String =
            if (id != null) foodsMap[id]?.name ?: "Keine Angabe" else "Keine Angabe"

        // Zeiten in Minuten (kannst du anpassen)
        val meals = listOf(
            MealItem(
                title = "Suppe",
                foodName = foodName(menu.soupId),
                foodType = "soup",
                timeMinutes = 11 * 60 + 30
            ),
            MealItem(
                title = "Hauptgericht 1",
                foodName = foodName(menu.m1Id),
                foodType = "main",
                timeMinutes = 12 * 60
            ),
            MealItem(
                title = "Hauptgericht 2",
                foodName = foodName(menu.m2Id),
                foodType = "main",
                timeMinutes = 12 * 60 + 15
            ),
            MealItem(
                title = "Dessert",
                foodName = foodName(menu.lunchDessertId),
                foodType = "dessert",
                timeMinutes = 13 * 60
            ),
            MealItem(
                title = "Abendessen 1",
                foodName = foodName(menu.a1Id),
                foodType = "main",
                timeMinutes = 17 * 60 + 30
            ),
            MealItem(
                title = "Abendessen 2",
                foodName = foodName(menu.a2Id),
                foodType = "main",
                timeMinutes = 18 * 60
            )
        )

        return DayMealsUi(
            calendar = calendar,
            label = label,
            weekdayCode = weekdayCode,
            menu = menu,
            meals = meals
        )
    }

    private fun calculateInitialMealIndex(
        nowMinutes: Int,
        day: DayMealsUi?
    ): Int {
        val meals = day?.meals ?: return 0
        if (meals.isEmpty()) return 0

        val idx = meals.indexOfFirst { it.timeMinutes >= nowMinutes }
        return if (idx == -1) meals.lastIndex else idx
    }
}

// Tag → "MO", "DI", ...
private fun Calendar.toWeekdayCode(): String =
    when (get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "MO"
        Calendar.TUESDAY -> "DI"
        Calendar.WEDNESDAY -> "MI"
        Calendar.THURSDAY -> "DO"
        Calendar.FRIDAY -> "FR"
        Calendar.SATURDAY -> "SA"
        Calendar.SUNDAY -> "SO"
        else -> "MO"
    }

// 6-Wochen-Zyklus (WeekNumber 1..6) ohne java.time
private fun getWeekNumberForDate(calendar: Calendar): Int {
    val base = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2025)
        set(Calendar.MONTH, Calendar.JANUARY) // 0
        set(Calendar.DAY_OF_MONTH, 6)        // Montag
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val diffMillis = calendar.timeInMillis - base.timeInMillis
    val millisPerWeek = 7L * 24L * 60L * 60L * 1000L
    val weeksBetween = diffMillis / millisPerWeek

    var index = (weeksBetween % 6).toInt()
    if (index < 0) index += 6
    return index + 1 // 1..6
}

private fun getGermanWeekdayFromCode(code: String): String =
    when (code) {
        "MO" -> "Montag"
        "DI" -> "Dienstag"
        "MI" -> "Mittwoch"
        "DO" -> "Donnerstag"
        "FR" -> "Freitag"
        "SA" -> "Samstag"
        "SO" -> "Sonntag"
        else -> code
    }
