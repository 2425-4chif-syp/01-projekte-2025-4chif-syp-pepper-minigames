package com.example.menu.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
        Text(
            text = "Program Menu",
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        val pagerState = rememberPagerState(initialPage = 0)
        HorizontalPager(
            count = 5,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> MenuItem(
                    imageRes = R.drawable.mitmachgeschichte,
                    title = "Mitmachgeschichte",
                    navController = navController,
                    route = "mitmachgeschichte_screen"
                )
                1 -> MenuItem(
                    imageRes = R.drawable.memory_game,
                    title = "Memory",
                    navController = navController,
                    route = "memory_screen"
                )
                2 -> MenuItem(
                    imageRes = R.drawable.tic_tac_toe,
                    title = "Tic Tac Toe",
                    navController = navController,
                    route = "tic_tac_toe_screen"
                )
                3 -> MenuItem(
                    imageRes = R.drawable.fang_den_dieb,
                    title = "Fang den Dieb",
                    navController = navController,
                    route = "fang_den_dieb_screen"
                )
                4 -> MenuItem(
                    imageRes = R.drawable.essensplan,
                    title = "Essensplan",
                    navController = navController,
                    route = "essensplan_screen"
                )
            }
        }
    }
}

@Composable
fun MenuItem(imageRes: Int, title: String, navController: NavHostController, route: String) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(0.8f)
            .aspectRatio(1f)
            .clickable { navController.navigate(route) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF9C4) // Sanftes Gelb für die Karten
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Text(
                text = title,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
