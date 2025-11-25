package com.pepper.mealplan.createMealPlan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// Meal selection steps
enum class MealStep {
    SOUP, MAIN, DESSERT, EVENING
}

class CreateMealPlanViewModel : ViewModel() {
    
    var selectedWeek by mutableStateOf<Int?>(null)
        private set
    
    var showDayView by mutableStateOf(false)
        private set
    
    var selectedDay by mutableStateOf<String?>(null)
        private set
    
    var showMealSelection by mutableStateOf(false)
        private set
    
    var currentMealStep by mutableStateOf(MealStep.SOUP)
        private set
    
    var completedDays by mutableStateOf<Set<String>>(emptySet())
        private set
    
    // Selected meal IDs for current day
    var selectedSoupId by mutableStateOf<Int?>(null)
        private set
    var selectedMainId by mutableStateOf<Int?>(null)
        private set
    var selectedDessertId by mutableStateOf<Int?>(null)
        private set
    var selectedEveningId by mutableStateOf<Int?>(null)
        private set
    
    fun selectWeek(weekNumber: Int) {
        selectedWeek = weekNumber
        showDayView = true
    }
    
    fun backToWeekSelection() {
        selectedWeek = null
        showDayView = false
        resetMealSelection()
    }
    
    fun selectDay(dayShort: String) {
        selectedDay = dayShort
        showMealSelection = true
        currentMealStep = MealStep.SOUP
    }
    
    fun backToDaySelection() {
        showMealSelection = false
        selectedDay = null
        resetMealSelection()
    }
    
    fun selectMeal(mealId: Int) {
        when (currentMealStep) {
            MealStep.SOUP -> {
                selectedSoupId = mealId
                currentMealStep = MealStep.MAIN
            }
            MealStep.MAIN -> {
                selectedMainId = mealId
                currentMealStep = MealStep.DESSERT
            }
            MealStep.DESSERT -> {
                selectedDessertId = mealId
                currentMealStep = MealStep.EVENING
            }
            MealStep.EVENING -> {
                selectedEveningId = mealId
                completeMealSelection()
            }
        }
    }
    
    private fun completeMealSelection() {
        selectedDay?.let { day ->
            completedDays = completedDays + day
        }
        backToDaySelection()
    }
    
    private fun resetMealSelection() {
        selectedSoupId = null
        selectedMainId = null
        selectedDessertId = null
        selectedEveningId = null
        currentMealStep = MealStep.SOUP
    }
}