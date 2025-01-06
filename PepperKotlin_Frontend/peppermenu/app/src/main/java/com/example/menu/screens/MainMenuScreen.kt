package com.example.menu.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    val pagerState = rememberPagerState(initialPage = 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFFCDD2),
                        Color(0xFFFFF59D),
                        Color(0xFFA5D6A7),
                        Color(0xFF90CAF9)
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titel des Menüs
        Text(
            text = "Program Menu",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723) // Dunkles Braun
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Horizontaler Pager für Programme
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
                    title = "Memoryspiel",
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
            .width(250.dp) // Breite der Karte
            .height(350.dp) // Höhe der Karte
            .clickable { navController.navigate(route) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0) // Sanfte Hintergrundfarbe der Karte
        ),
        elevation = CardDefaults.elevatedCardElevation(8.dp) // Kleinere Elevation für flacheren Effekt
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Innerer Abstand der Karte
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Zentriert Bild und Text
        ) {
            // Verkleinertes Bild
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .size(200.dp) // Kleinere Bildgröße
                    .padding(bottom = 8.dp) // Abstand zwischen Bild und Text
            )
            // Text unter dem Bild
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp, // Kleinere Schriftgröße
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                ),
                modifier = Modifier.padding(top = 4.dp) // Abstand über dem Text
            )
        }
    }
}
