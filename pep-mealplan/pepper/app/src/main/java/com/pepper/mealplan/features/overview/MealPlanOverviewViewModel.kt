package com.pepper.mealplan.features.overview

import com.pepper.mealplan.BuildConfig

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepper.mealplan.data.foods.FoodsRepository
import com.pepper.mealplan.data.menu.MenuRepository
import com.pepper.mealplan.data.order.MealOrderRepositoryProvider
import com.pepper.mealplan.data.order.MealSlot
import com.pepper.mealplan.data.orders.OrdersRepository
import com.pepper.mealplan.data.residents.ResidentsRepository
import com.pepper.mealplan.network.dto.ApiMealPlanDto
import com.pepper.mealplan.network.dto.ApiFoodDto
import com.pepper.mealplan.network.dto.ExportOrderDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


// Eine Mahlzeit (z.B. Suppe, Hauptgericht, Dessert, Abendessen)
data class MealItem(
    val title: String,
    val foodName: String,
    val foodType: String,
    val timeMinutes: Int,
    val foodId: Int? = null,
    val pictureId: Int? = null
)

// UI-Daten für einen Tag
data class DayMealsUi(
    val calendar: Calendar,
    val dateKey: String,       // yyyy-MM-dd (für Orders Export)
    val label: String,
    val weekdayCode: String,
    val menu: ApiMealPlanDto?,
    val meals: List<MealItem>
)

data class DayOrderStatus(
    val lunchOrdered: Boolean,
    val dinnerOrdered: Boolean
)

class MealPlanOverviewViewModel(
    private val foundPerson: String = ""
) : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    var threeDayMeals by mutableStateOf<List<DayMealsUi>>(emptyList())
        private set

    var initialMealIndexToday by mutableStateOf(0)
        private set

    private val menuRepository = MenuRepository()
    private val foodsRepository = FoodsRepository()
    private val residentsRepository = ResidentsRepository()
    private val ordersRepository = OrdersRepository()

    private var foodsMap by mutableStateOf<Map<Int, ApiFoodDto>>(emptyMap())

    // Bestellung-Status pro Tag (dateKey -> Status)
    private var orderStatusByDay by mutableStateOf<Map<String, DayOrderStatus>>(emptyMap())

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    init {
        refreshData()
    }

    fun getOrderStatus(dateKey: String): DayOrderStatus? = orderStatusByDay[dateKey]

    fun refreshData() {
        viewModelScope.launch {
            isLoading = true

            // 1) Foods laden (für Bilder / Fallback)
            val foodsResult = foodsRepository.getAllFoods()
            foodsResult.onSuccess { foods ->
                foodsMap = foods.filterNotNull().associateBy { it.id ?: 0 }
            }.onFailure { error ->
                println("Fehler beim Laden der Foods: ${error.message}")
            }

            val todayCal = Calendar.getInstance()
            val personId = resolvePersonId()

            // 2) Tage + Menüs laden
            val days = (0..2).map { offset ->
                val cal = (todayCal.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, offset)
                }

                val weekdayCode = cal.toWeekdayCode()
                val weekNumber = getWeekNumberForDate(cal)
                val weekDay = cal.toWeekDayIndex()
                val dateKey = dateFormat.format(cal.time)

                var menu: ApiMealPlanDto? = null
                val menuResult = menuRepository.getMenuForDate(weekNumber, weekDay)
                menuResult.onSuccess { apiMenu ->
                    menu = apiMenu
                }.onFailure { error ->
                    println("Fehler beim Laden des Menüs für Tag $weekDay, Woche $weekNumber: ${error.message}")
                }

                val orderForPerson = loadOrderForPerson(dateKey, personId)

                println("MENU DEBUG: soup=${menu?.soup?.name}, lunch1=${menu?.lunch1?.name}, pic=${menu?.lunch1?.picture?.id}")

                buildDayMealsUi(
                    calendar = cal,
                    dateKey = dateKey,
                    indexFromToday = offset,
                    weekdayCode = weekdayCode,
                    menu = menu,
                    orderForPerson = orderForPerson
                )

            }

            threeDayMeals = days

            // alle Food-IDs sammeln
            val foodIds = threeDayMeals
                .flatMap { it.meals }
                .mapNotNull { it.foodId }
                .distinct()

            val foodIdToPictureId = mutableMapOf<Int, Int?>()

            for (fid in foodIds) {
                val res = foodsRepository.getFoodById(fid)
                val food = res.getOrNull()
                foodIdToPictureId[fid] = food?.picture?.id
            }

            // Meals aktualisieren -> pictureId setzen
            threeDayMeals = threeDayMeals.map { day ->
                day.copy(
                    meals = day.meals.map { meal ->
                        val pid = meal.foodId?.let { foodIdToPictureId[it] } ?: meal.pictureId
                        meal.copy(pictureId = pid)
                    }
                )
            }

            // 3) Bestellung-Status aus Backend ableiten (nur Mittag/Abend relevant)
            // Repository ist bei dir korrekt angebunden (Export + Person)
            val missingMeals = MealOrderRepositoryProvider.repository
                .getMissingMealsForNextDays(foundPerson, days = 3)

            val statusMap = mutableMapOf<String, DayOrderStatus>()
            days.forEach { day ->
                val dateKey = day.dateKey
                val missingForDay = missingMeals.filter { it.dateKey == dateKey }

                val lunchMissing = missingForDay.any { it.slot == MealSlot.MAIN1 || it.slot == MealSlot.MAIN2 }
                val dinnerMissing = missingForDay.any { it.slot == MealSlot.DINNER1 || it.slot == MealSlot.DINNER2 }

                statusMap[dateKey] = DayOrderStatus(
                    lunchOrdered = !lunchMissing,
                    dinnerOrdered = !dinnerMissing
                )
            }
            orderStatusByDay = statusMap

            // 4) Initial-Index für "Heute"
            val nowMinutes = todayCal.get(Calendar.HOUR_OF_DAY) * 60 + todayCal.get(Calendar.MINUTE)
            val todayUi = threeDayMeals.firstOrNull()
            initialMealIndexToday = calculateInitialMealIndex(nowMinutes, todayUi)

            delay(200)
            isLoading = false
        }
    }

    private fun buildDayMealsUi(
        calendar: Calendar,
        dateKey: String,
        indexFromToday: Int,
        weekdayCode: String,
        menu: ApiMealPlanDto?,
        orderForPerson: ExportOrderDto?
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
                dateKey = dateKey,
                label = label,
                weekdayCode = weekdayCode,
                menu = null,
                meals = emptyList()
            )
        }

        fun foodName(food: ApiFoodDto?): String = food?.name ?: "Keine Angabe"

        fun resolvePictureId(food: ApiFoodDto?): Int? {
            // 1) wenn Menü-Food picture.id hat
            val direct = food?.picture?.id
            if (direct != null) return direct

            // 2) sonst aus FoodsMap über Food-ID holen
            val foodId = food?.id ?: return null
            return foodsMap[foodId]?.picture?.id
        }

        val meals = listOf(
            MealItem(
                title = "Suppe",
                foodName = menu.soup?.name ?: "Keine Angabe",
                foodType = "soup",
                timeMinutes = 11 * 60 + 30,
                foodId = menu.soup?.id,
                pictureId = menu.soup?.picture?.id
            ),
            MealItem(
                title = "Hauptgericht",
                foodName = orderForPerson?.selectedLunch?.name
                    ?: menu.lunch1?.name
                    ?: "Keine Angabe",
                foodType = "main",
                timeMinutes = 12 * 60,
                foodId = orderForPerson?.selectedLunch?.id ?: menu.lunch1?.id,
                pictureId = orderForPerson?.selectedLunch?.picture?.id ?: menu.lunch1?.picture?.id
            ),
            MealItem(
                title = "Dessert",
                foodName = menu.lunchDessert?.name ?: "Keine Angabe",
                foodType = "dessert",
                timeMinutes = 13 * 60,
                foodId = menu.lunchDessert?.id,
                pictureId = menu.lunchDessert?.picture?.id
            ),
            MealItem(
                title = "Abendessen",
                foodName = orderForPerson?.selectedDinner?.name
                    ?: menu.dinner1?.name
                    ?: "Keine Angabe",
                foodType = "main",
                timeMinutes = 17 * 60 + 30,
                foodId = orderForPerson?.selectedDinner?.id ?: menu.dinner1?.id,
                pictureId = orderForPerson?.selectedDinner?.picture?.id ?: menu.dinner1?.picture?.id
            )
        )

        return DayMealsUi(
            calendar = calendar,
            dateKey = dateKey,
            label = label,
            weekdayCode = weekdayCode,
            menu = menu,
            meals = meals
        )
    }

    private fun calculateInitialMealIndex(nowMinutes: Int, day: DayMealsUi?): Int {
        val meals = day?.meals ?: return 0
        if (meals.isEmpty()) return 0
        val idx = meals.indexOfFirst { it.timeMinutes >= nowMinutes }
        return if (idx == -1) meals.lastIndex else idx
    }

    private suspend fun resolvePersonId(): Int? {
        val normalizedFoundPerson = foundPerson.trim().replace(Regex("\\s+"), " ")
        val residents = residentsRepository.getResidents().getOrNull().orEmpty()
        return residents.firstOrNull {
            "${it.firstname} ${it.lastname}"
                .trim()
                .replace(Regex("\\s+"), " ")
                .equals(normalizedFoundPerson, ignoreCase = true)
        }?.id
    }

    private suspend fun loadOrderForPerson(dateKey: String, personId: Int?): ExportOrderDto? {
        if (personId == null) return null
        val exportOrders = ordersRepository.getExportedOrders(dateKey).getOrNull().orEmpty()
        return exportOrders.firstOrNull { it.person.id == personId && it.date == dateKey }
    }
}

// ---------- Helpers (wie bei dir) ----------

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

private fun Calendar.toWeekDayIndex(): Int =
    when (get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }

private fun getWeekNumberForDate(calendar: Calendar): Int {
    val base = Calendar.getInstance().apply {
        val parts = BuildConfig.WEEK1_BASE_DATE.split("-")
        set(Calendar.YEAR, parts[0].toInt())
        set(Calendar.MONTH, parts[1].toInt() - 1)
        set(Calendar.DAY_OF_MONTH, parts[2].toInt())
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val cal = (calendar.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val millisPerWeek = 7L * 24L * 60L * 60L * 1000L
    val diffWeeks = ((cal.timeInMillis - base.timeInMillis) / millisPerWeek).toInt()

    var index = diffWeeks % 4
    if (index < 0) index += 4
    return index + 1
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
