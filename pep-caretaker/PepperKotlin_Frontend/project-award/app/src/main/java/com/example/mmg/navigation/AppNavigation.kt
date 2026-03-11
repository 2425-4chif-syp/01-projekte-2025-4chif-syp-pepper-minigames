package com.example.project_award.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project_award.presentation.MmgScreen
import com.example.project_award.presentation.StepScreen
import com.example.project_award.viewmodel.MmgViewModel

@Composable
fun AppNavigaton(navController: NavHostController, mmgViewModel: MmgViewModel) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ){
        composable("home"){ MmgScreen(navController = navController, viewModel = mmgViewModel)}
        composable("step"){ StepScreen(navController = navController, viewModel = mmgViewModel)}
    }
}