package com.pepper.mealplan.createMealPlan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CreateMealPlanViewModel : ViewModel() {
    
    var selectedWeek by mutableStateOf<Int?>(null)
        private set
    
    var showDayView by mutableStateOf(false)
        private set
    
    fun selectWeek(weekNumber: Int) {
        selectedWeek = weekNumber
        showDayView = true
    }
    
    fun backToWeekSelection() {
        selectedWeek = null
        showDayView = false
    }
}