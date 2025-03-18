import android.content.Intent
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale // Import für ContentScale
import com.example.menu.RoboterActions
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainMenuScreen(navController: NavHostController) {
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope() // Coroutine-Scope für suspend-Funktionen

    LaunchedEffect(key1 = Unit){
        Log.d("QiContext:","${RoboterActions.qiContext}")
        RoboterActions.speak("Was wollen Sie machen?")
    }

    // Liste mit Bildquellen, Titeln und Package-Namen
    val menuItems = listOf(
        Pair(R.drawable.mitmachgeschichte, "Mitmachgeschichte" to "com.example.mitmachgeschichte"),
        Pair(R.drawable.memory_game, "Memory" to "com.example.memory"),
        Pair(R.drawable.tic_tac_toe, "Tic Tac Toe" to "com.example.tictactoe"),
        Pair(R.drawable.fang_den_dieb, "Fang den Dieb" to "com.example.fangdendieb"),
        Pair(R.drawable.essensplan, "Essensplan" to "com.example.essensplan")
    )

    // Animation für die Farben
    val infiniteTransition = rememberInfiniteTransition()
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF2196F3), // Blau
        targetValue = Color(0xFF64B5F6), // Hellblau
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 30001, easing = LinearEasing),
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
                    title = menuItems[page].second.first, // Der Titel der App
                    navController = navController,
                    packageName = menuItems[page].second.second // Der Package-Name der App
                )
            }

            // Pfeil-Icon für "nächste Seite"
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Swipe Right",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(100.dp)
                    .clickable {
                        // Zur nächsten Seite navigieren (innerhalb einer Coroutine)
                        coroutineScope.launch {
                            val nextPage = (pagerState.currentPage + 1) % menuItems.size
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }
            )

            // Pfeil-Icon für "vorherige Seite"
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Swipe Left",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(100.dp)
                    .clickable {
                        // Zur vorherigen Seite navigieren (innerhalb einer Coroutine)
                        coroutineScope.launch {
                            val previousPage =
                                (pagerState.currentPage - 1 + menuItems.size) % menuItems.size
                            pagerState.animateScrollToPage(previousPage)
                        }
                    }
            )
        }
    }
}

@Composable
fun MenuItem(imageRes: Int, title: String, navController: NavHostController, packageName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                // Navigiere zum LoginScreen statt direkt zur App
                navController.navigate("login_screen") // Dies führt den Benutzer zum LoginScreen
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .align(Alignment.TopCenter)
                .padding(8.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 70.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}