package com.example.mmg.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mmg.presentation.MmgScreen
import com.example.mmg.presentation.StepScreen

@Composable
fun AppNavigaton(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ){
        composable("home"){ MmgScreen(navController = navController)}
        composable("step"){ StepScreen(navController = navController)}
    }
}