package com.pepper.mealplan.MealPlanOverview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.pepper.mealplan.data.DataInserts
import com.pepper.mealplan.network.dto.FoodDto
import com.pepper.mealplan.network.dto.MenuDto

class MealPlanOverviewViewModel : ViewModel() {

    var currentWeek by mutableStateOf(1)
        private set

    var isLoading by mutableStateOf(true) // Start mit Loading = true
        private set

    // Lazy initialization für bessere Performance
    private val allData by lazy { DataInserts.getAllData() }
    
    // Cache für Foods für bessere Performance
    private val foodsMap by lazy { 
        allData.foods.associateBy { it.id }
    }
    
    val weekMenus: List<MenuDto>
        get() = allData.menus.filter { it.weekNumber == currentWeek }

    init {
        // Initialisiere die Daten beim Start
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            // Simuliere kurze Ladezeit für bessere UX, auch bei synchronen Daten
            delay(500)
            // Zugriff auf allData triggert die lazy initialization
            val data = allData
            isLoading = false
        }
    }

    fun navigateToNextWeek() {
        if (currentWeek < 6) {
            loadWeekData {
                currentWeek++
            }
        }
    }

    fun navigateToPreviousWeek() {
        if (currentWeek > 1) {
            loadWeekData {
                currentWeek--
            }
        }
    }

    private fun loadWeekData(onWeekChange: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            onWeekChange()
            // Simuliere eine kurze Ladezeit für flüssigere UX
            delay(300)
            isLoading = false
        }
    }

    fun getFoodById(id: Int?): FoodDto? {
        return if (id != null) foodsMap[id] else null
    }
}