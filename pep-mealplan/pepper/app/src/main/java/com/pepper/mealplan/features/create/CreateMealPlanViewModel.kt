package com.pepper.mealplan.features.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepper.mealplan.BuildConfig
import com.pepper.mealplan.RoboterActions
import com.pepper.mealplan.data.orders.OrdersRepository
import com.pepper.mealplan.data.residents.ResidentsRepository
import com.pepper.mealplan.data.order.MealOrderRepositoryProvider
import com.pepper.mealplan.data.order.MealSlot
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

enum class CreateStage { DAY_PICK, MEALTYPE_PICK, MEAL_SELECTION }
enum class MissingMealType { LUNCH, DINNER }

// Nur das brauchen wir in der Auswahl
enum class MealStep { MAIN, EVENING }

data class NextDayUi(
    val label: String,        // Heute/Morgen/Übermorgen
    val dateKey: String,      // yyyy-MM-dd (für Export/Upsert)
    val displayDate: String,  // dd.MM.yyyy
    val weekNumber: Int,      // 1..4
    val dayShort: String      // MO/DI/...
)

class CreateMealPlanViewModel(
    private val foundPerson: String = ""
) : ViewModel() {

    var stage by mutableStateOf(CreateStage.DAY_PICK)
        private set

    // Nur Tage, wo noch was fehlt (diese Liste schrumpft)
    var pendingDays by mutableStateOf<List<NextDayUi>>(emptyList())
        private set

    var selectedDay by mutableStateOf<NextDayUi?>(null)
        private set

    var missingLunch by mutableStateOf(false)
        private set

    var missingDinner by mutableStateOf(false)
        private set

    var currentMealStep by mutableStateOf(MealStep.MAIN)
        private set

    // Signale für UI-Navigation
    var navigateToMenu by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successfulOrderVersion by mutableStateOf(0)
        private set

    private val ordersRepository = OrdersRepository()
    private val residentsRepository = ResidentsRepository()
    private val mealRepo = MealOrderRepositoryProvider.repository

    private val lunchTimeMinutes = 12 * 60
    private val dinnerTimeMinutes = 17 * 60 + 30

    private var personId: Int? = null

    // Wenn nur eine fehlt: andere aus Export übernehmen
    private var existingLunchId: Int? = null
    private var existingDinnerId: Int? = null

    // Wenn beide fehlen: erst nach der 2. Auswahl speichern
    private var tempLunchId: Int? = null
    private var tempDinnerId: Int? = null

    init {
        loadPersonIdAndBuildPendingDays()
    }

    private fun loadPersonIdAndBuildPendingDays() {
        viewModelScope.launch {
            val residentsRes = residentsRepository.getResidents()
            residentsRes.onSuccess { residents ->
                val resident = residents.find {
                    "${it.firstname} ${it.lastname}".equals(foundPerson, ignoreCase = true)
                }
                personId = resident?.id
            }
            buildPendingDays()
        }
    }

    private fun buildPendingDays() {
        val nextThree = buildNextThreeDays()

        // Missing für 3 Tage holen
        val missingAll = mealRepo.getMissingMealsForNextDays(foundPerson, days = 3)

        val filtered = nextThree.filter { day ->
            val miss = missingAll.filter { it.dateKey == day.dateKey }
            val rawLunchMiss = miss.any { it.slot == MealSlot.MAIN1 || it.slot == MealSlot.MAIN2 }
            val rawDinnerMiss = miss.any { it.slot == MealSlot.DINNER1 || it.slot == MealSlot.DINNER2 }
            val (effectiveLunchMiss, effectiveDinnerMiss) =
                applyOrderingWindow(day.dateKey, rawLunchMiss, rawDinnerMiss)
            effectiveLunchMiss || effectiveDinnerMiss
        }

        pendingDays = filtered

        // Wenn nichts fehlt -> sofort Menü
        if (pendingDays.isEmpty()) {
            navigateToMenu = true
        }
    }

    private fun buildNextThreeDays(): List<NextDayUi> {
        val today = Calendar.getInstance()

        fun toDateKey(cal: Calendar): String =
            String.format(
                Locale.US, "%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )

        fun toDisplayDate(cal: Calendar): String =
            String.format(
                Locale.GERMAN, "%02d.%02d.%04d",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR)
            )

        fun dayShort(cal: Calendar): String = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MO"
            Calendar.TUESDAY -> "DI"
            Calendar.WEDNESDAY -> "MI"
            Calendar.THURSDAY -> "DO"
            Calendar.FRIDAY -> "FR"
            Calendar.SATURDAY -> "SA"
            Calendar.SUNDAY -> "SO"
            else -> "MO"
        }

        return (0..2).map { offset ->
            val cal = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, offset) }
            val label = when (offset) {
                0 -> "Heute"
                1 -> "Morgen"
                else -> "Übermorgen"
            }

            NextDayUi(
                label = label,
                dateKey = toDateKey(cal),
                displayDate = toDisplayDate(cal),
                weekNumber = getWeekNumberForDate(cal),
                dayShort = dayShort(cal)
            )
        }
    }

    fun onBackPressedToMenu() {
        navigateToMenu = true
    }

    fun onDayClicked(day: NextDayUi) {
        selectedDay = day
        errorMessage = null

        val missing = mealRepo.getMissingMealsForNextDays(foundPerson, days = 3)
            .filter { it.dateKey == day.dateKey }

        val rawLunchMissing = missing.any { it.slot == MealSlot.MAIN1 || it.slot == MealSlot.MAIN2 }
        val rawDinnerMissing = missing.any { it.slot == MealSlot.DINNER1 || it.slot == MealSlot.DINNER2 }
        val (effectiveLunchMissing, effectiveDinnerMissing) =
            applyOrderingWindow(day.dateKey, rawLunchMissing, rawDinnerMissing)

        missingLunch = effectiveLunchMissing
        missingDinner = effectiveDinnerMissing

        if (!missingLunch && !missingDinner) {
            // Tag ist für Bestellungen nicht mehr relevant (z.B. Mahlzeitenzeit bereits vorbei).
            pendingDays = pendingDays.filterNot { it.dateKey == day.dateKey }
            selectedDay = null
            stage = CreateStage.DAY_PICK
            if (pendingDays.isEmpty()) {
                navigateToMenu = true
            }
            return
        }

        // Export holen, damit wir existingLunch/ existingDinner übernehmen können
        viewModelScope.launch {
            val pid = personId ?: return@launch
            val exportRes = ordersRepository.getExportedOrders(day.dateKey)
            // Export-Endpoint kann mehrere Tage liefern: daher Person + Datum filtern.
            val orderForPerson = exportRes.getOrNull()?.firstOrNull {
                it.person.id == pid && it.date == day.dateKey
            }

            existingLunchId = orderForPerson?.selectedLunch?.id
            existingDinnerId = orderForPerson?.selectedDinner?.id

            tempLunchId = null
            tempDinnerId = null

            if (missingLunch && missingDinner) {
                stage = CreateStage.MEALTYPE_PICK
            } else if (missingLunch) {
                startSelection(MissingMealType.LUNCH)
            } else if (missingDinner) {
                startSelection(MissingMealType.DINNER)
            } else {
                stage = CreateStage.DAY_PICK
            }
        }
    }

    private fun applyOrderingWindow(
        dateKey: String,
        lunchMissing: Boolean,
        dinnerMissing: Boolean
    ): Pair<Boolean, Boolean> {
        // Nur für Heute: ausschließlich die nächste offene Mahlzeit darf bestellt werden.
        if (dateKey != todayDateKey()) return lunchMissing to dinnerMissing

        val now = nowMinutes()
        return when {
            now >= dinnerTimeMinutes -> false to false
            now >= lunchTimeMinutes -> false to dinnerMissing
            lunchMissing -> true to false
            else -> false to dinnerMissing
        }
    }

    private fun todayDateKey(): String {
        val cal = Calendar.getInstance()
        return String.format(
            Locale.US, "%04d-%02d-%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun nowMinutes(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    }

    fun onMealTypeClicked(type: MissingMealType) {
        startSelection(type)
    }

    fun backFromMealTypePicker() {
        stage = CreateStage.DAY_PICK
    }

    private fun startSelection(type: MissingMealType) {
        currentMealStep = if (type == MissingMealType.LUNCH) MealStep.MAIN else MealStep.EVENING
        stage = CreateStage.MEAL_SELECTION
    }

    fun onSelectionBack() {
        // Wenn beide fehlen -> zurück zur MealType-Auswahl, sonst zurück zur Tagesliste
        stage = if (missingLunch && missingDinner) CreateStage.MEALTYPE_PICK else CreateStage.DAY_PICK
    }

    fun onFoodChosen(foodId: Int) {
        errorMessage = null
        val day = selectedDay ?: return

        // Fall A: beide fehlen -> erst sammeln, dann nach 2. Auswahl speichern
        if (missingLunch && missingDinner) {
            if (currentMealStep == MealStep.MAIN) {
                tempLunchId = foodId
                // Direkt Abend-Auswahl zeigen
                currentMealStep = MealStep.EVENING
                stage = CreateStage.MEAL_SELECTION
                // Pepper kann optional kurz sagen, dass jetzt Abendessen kommt:
                RoboterActions.stopSpeaking()
                RoboterActions.speak("Gut. Jetzt fehlt noch das Abendessen für ${day.label}.")
                return
            } else {
                tempDinnerId = foodId
                // Jetzt haben wir beide -> speichern
                val lunchId = tempLunchId
                val dinnerId = tempDinnerId
                if (lunchId == null || dinnerId == null) {
                    errorMessage = "Fehler: Auswahl unvollständig"
                    return
                }
                submitUpsert(day, lunchId, dinnerId)
                return
            }
        }

        // Fall B: nur Lunch fehlt -> sofort speichern mit bestehendem Dinner
        if (missingLunch && !missingDinner) {
            val dinnerId = existingDinnerId
            if (dinnerId == null) {
                errorMessage = "Abendessen ist nicht vorhanden – bitte später beide auswählen."
                return
            }
            submitUpsert(day, foodId, dinnerId)
            return
        }

        // Fall C: nur Dinner fehlt -> sofort speichern mit bestehendem Lunch
        if (!missingLunch && missingDinner) {
            val lunchId = existingLunchId
            if (lunchId == null) {
                errorMessage = "Mittagessen ist nicht vorhanden – bitte später beide auswählen."
                return
            }
            submitUpsert(day, lunchId, foodId)
            return
        }
    }

    private fun submitUpsert(day: NextDayUi, lunchId: Int, dinnerId: Int) {
        viewModelScope.launch {
            val pid = personId
            if (pid == null) {
                errorMessage = "Person nicht gefunden"
                return@launch
            }

            val res = ordersRepository.upsertOrder(
                date = day.dateKey,
                personId = pid,
                selectedLunchId = lunchId,
                selectedDinnerId = dinnerId
            )

            res.onSuccess {
                successfulOrderVersion++

                // Tag ist erledigt -> aus Liste entfernen
                pendingDays = pendingDays.filterNot { it.dateKey == day.dateKey }

                // Reset
                selectedDay = null
                missingLunch = false
                missingDinner = false
                existingLunchId = null
                existingDinnerId = null
                tempLunchId = null
                tempDinnerId = null
                errorMessage = null

                // Wenn keine Tage mehr übrig -> Menü
                if (pendingDays.isEmpty()) {
                    navigateToMenu = true
                } else {
                    stage = CreateStage.DAY_PICK
                }
            }.onFailure { e ->
                errorMessage = "Fehler beim Speichern: ${e.message}"
            }
        }
    }

    private fun getWeekNumberForDate(calendar: Calendar): Int {
        val parts = BuildConfig.WEEK1_BASE_DATE.split("-")
        val baseYear = parts[0].toInt()
        val baseMonth = parts[1].toInt() - 1
        val baseDay = parts[2].toInt()

        val base = Calendar.getInstance().apply {
            set(Calendar.YEAR, baseYear)
            set(Calendar.MONTH, baseMonth)
            set(Calendar.DAY_OF_MONTH, baseDay)
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
}
