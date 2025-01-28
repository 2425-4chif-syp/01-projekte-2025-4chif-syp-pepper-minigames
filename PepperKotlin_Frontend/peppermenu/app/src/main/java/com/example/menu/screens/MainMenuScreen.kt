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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainMenuScreen(navController: NavHostController) {
    val pagerState = rememberPagerState(initialPage = 0)

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
        // Horizontaler Pager für Programme
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
            .fillMaxSize() // Füllt den verfügbaren Platz
            .padding(16.dp)
            .clickable { navController.navigate(route) }, // Navigation bleibt erhalten
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f) // Breite angepasst, um Bild besser darzustellen
                .aspectRatio(1f) // Verhältnis beibehalten
                .padding(bottom = 16.dp), // Abstand nach unten
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