package com.example.menu.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.menu.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainMenuScreen(navController: NavHostController) {
    // Animation für die Hintergrundfarben
    val infiniteTransition = rememberInfiniteTransition()
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF2196F3),
        targetValue = Color(0xFF64B5F6),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFFFF8A65),
        targetValue = Color(0xFFBBDEFB),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(color1, color2)
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titel des Menüs
        Text(
            text = "Program Menu",
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Horizontaler Pager für Programme
        val pagerState = rememberPagerState(initialPage = 0)
        HorizontalPager(
            count = 5,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> MenuItem(
                    imageRes = R.drawable.mitmachgeschichte,
                    navController = navController,
                    route = "mitmachgeschichte_screen"
                )
                1 -> MenuItem(
                    imageRes = R.drawable.memory_game,
                    navController = navController,
                    route = "memory_screen"
                )
                2 -> MenuItem(
                    imageRes = R.drawable.tic_tac_toe,
                    navController = navController,
                    route = "tic_tac_toe_screen"
                )
                3 -> MenuItem(
                    imageRes = R.drawable.fang_den_dieb,
                    navController = navController,
                    route = "fang_den_dieb_screen"
                )
                4 -> MenuItem(
                    imageRes = R.drawable.essensplan,
                    navController = navController,
                    route = "essensplan_screen"
                )
            }
        }
    }
}

@Composable
fun MenuItem(imageRes: Int, navController: NavHostController, route: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable { navController.navigate(route) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null
            )
        }
        Text(
            text = route.replace("_screen", "").replace("_", " ").capitalize(),
            fontSize = 18.sp
        )
    }
}
