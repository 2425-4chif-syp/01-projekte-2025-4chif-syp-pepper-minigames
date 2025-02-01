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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.ui.layout.ContentScale // Import für ContentScale

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainMenuScreen(navController: NavHostController) {
    val pagerState = rememberPagerState(initialPage = 0)

    // Liste mit Bildquellen und dazugehörigen Titeln
    val menuItems = listOf(
        Pair(R.drawable.mitmachgeschichte, "Mitmachgeschichte"),
        Pair(R.drawable.memory_game, "Memory"),
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
                .fillMaxWidth(),
//                .height(90.dp), // Optional: Höhe anpassen, falls nötig
            contentAlignment = Alignment.Center // Zentriert den Inhalt in der Box
        ) {

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
                    title = menuItems[page].second,
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
                tint = Color.White, // Weiße Farbe für den Pfeil
                modifier = Modifier
                    .align(Alignment.CenterEnd) // Fixiert es auf die rechte Seite
                    .padding(end = 16.dp)
                    .size(100.dp)
            )
            Icon(
                imageVector = Icons.Default.ArrowBack, // Pfeil nach links
                contentDescription = "Swipe Left",
                tint = Color.White, // Weiße Farbe für den Pfeil
                modifier = Modifier
                    .align(Alignment.CenterStart) // Fixiert es auf die linke Seite
                    .padding(start = 16.dp) // Abstand von der linken Seite
                    .size(100.dp) // Größe des Icons
            )

        }
    }
}

@Composable
fun MenuItem(imageRes: Int, title: String, navController: NavHostController, route: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { navController.navigate(route) },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Halbtransparenter Hintergrund für besseren Kontrast
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f)) // Dunkler Hintergrund mit Transparenz
                .align(Alignment.TopCenter) // Oben zentrieren
                .padding(8.dp)
        ) {
            Text(
                text = title,
                color = Color.White, // Weißer Text für besseren Kontrast
                fontSize = 70.sp, // Große Schriftgröße
                fontWeight = FontWeight.Bold, // Fettschrift
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
