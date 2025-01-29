import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.menu.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.ArrowForward

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainMenuScreen(navController: NavHostController) {
    val pagerState = rememberPagerState(initialPage = 0)

    // Liste mit Bildquellen und dazugehörigen Titeln
    val menuItems = listOf(
        Pair(R.drawable.mitmachgeschichte, "Mitmachgeschichte"),
        Pair(R.drawable.memory_game, "Memory Spiel"),
        Pair(R.drawable.tic_tac_toe, "Tic Tac Toe"),
        Pair(R.drawable.fang_den_dieb, "Fang den Dieb"),
        Pair(R.drawable.essensplan, "Essensplan")
    )

    // Animation für die Farben
    val infiniteTransition = rememberInfiniteTransition()
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF2196F3), // Blau
        targetValue = Color(0xFF64B5F6), // Hellblau
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFFFF8A65), // Korallorange
        targetValue = Color(0xFFBBDEFB), // Sehr helles Blau
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
        // Stilvoller Titel in der Mitte
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp), // Optional: Höhe anpassen, falls nötig
            contentAlignment = Alignment.Center // Zentriert den Inhalt in der Box
        ) {
            // Schwarzer Text als Hintergrund (leicht versetzt)
            Text(
                text = menuItems[pagerState.currentPage].second,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black, // Randfarbe
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(x = 2.dp, y = -5.dp) // Leichte Verschiebung nach oben
            )
            // Weißer Text darüber
            Text(
                text = menuItems[pagerState.currentPage].second,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White, // Hauptfarbe
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = -5.dp) // Weißer Text auch leicht nach oben
            )
        }



        // Horizontaler Pager für Programme
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                count = menuItems.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                MenuItem(
                    imageRes = menuItems[page].first,
                    navController = navController,
                    route = when (page) {
                        0 -> "mitmachgeschichte_screen"
                        1 -> "memory_screen"
                        2 -> "tic_tac_toe_screen"
                        3 -> "fang_den_dieb_screen"
                        4 -> "essensplan_screen"
                        else -> "main_menu"
                    }
                )
            }

            // Icon außerhalb des Pagers, fix auf der rechten Seite
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Swipe Right",
                tint = Color.Blue,
                modifier = Modifier
                    .align(Alignment.CenterEnd) // Fixiert es auf die rechte Seite
                    .padding(end = 16.dp)
                    .size(100.dp)
            )
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
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}