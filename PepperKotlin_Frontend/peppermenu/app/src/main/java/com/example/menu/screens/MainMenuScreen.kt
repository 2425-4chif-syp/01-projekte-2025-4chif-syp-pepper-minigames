import android.content.Intent
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
<<<<<<< HEAD
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
=======
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
>>>>>>> main
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
<<<<<<< HEAD
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale // Import für ContentScale
import com.example.menu.RoboterActions
=======
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Icon
import androidx.compose.runtime.rememberCoroutineScope
>>>>>>> main
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainMenuScreen(navController: NavHostController) {
    val pagerState = rememberPagerState(initialPage = 0)
<<<<<<< HEAD
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
=======
    val coroutineScope = rememberCoroutineScope()
>>>>>>> main

    // Liste mit Bildquellen und dazugehörigen Titeln
    val menuItems = listOf(
        Pair(R.drawable.mitmachgeschichte, "Mitmachgeschichte"),
        Pair(R.drawable.memory_game, "Memory Spiel"),
        Pair(R.drawable.tic_tac_toe, "Tic Tac Toe"),
        Pair(R.drawable.fang_den_dieb, "Fang den Dieb"),
        Pair(R.drawable.essensplan, "Essensplan")
    )

    // Animation für den Hintergrund
    val infiniteTransition = rememberInfiniteTransition()
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF2196F3),
        targetValue = Color(0xFF64B5F6),
        animationSpec = infiniteRepeatable(
<<<<<<< HEAD
            animation = tween(durationMillis = 30001, easing = LinearEasing),
=======
            animation = tween(durationMillis = 2999, easing = LinearEasing),
>>>>>>> main
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
                Brush.horizontalGradient(colors = listOf(color1, color2))
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
<<<<<<< HEAD
        // Horizontaler Pager für Programme
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
=======
        // Horizontaler Pager für die Bilder
        Box(modifier = Modifier.fillMaxSize()) {
>>>>>>> main
            HorizontalPager(
                count = menuItems.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                MenuItem(
                    imageRes = menuItems[page].first,
<<<<<<< HEAD
                    title = menuItems[page].second.first, // Der Titel der App
                    navController = navController,
                    packageName = menuItems[page].second.second // Der Package-Name der App
                )
            }

            // Pfeil-Icon für "nächste Seite"
=======
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

            // Pfeil rechts (zur nächsten Seite, mit Rotation)
>>>>>>> main
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Swipe Right",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
<<<<<<< HEAD
                    .padding(end = 16.dp)
                    .size(100.dp)
                    .clickable {
                        // Zur nächsten Seite navigieren (innerhalb einer Coroutine)
=======
                    .padding(end = 32.dp)
                    .size(60.dp)
                    .clickable {
>>>>>>> main
                        coroutineScope.launch {
                            val nextPage = (pagerState.currentPage + 1) % menuItems.size
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }
            )

<<<<<<< HEAD
            // Pfeil-Icon für "vorherige Seite"
=======
            // Pfeil links (zur vorherigen Seite, mit Rotation)
>>>>>>> main
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Swipe Left",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
<<<<<<< HEAD
                    .padding(start = 16.dp)
                    .size(100.dp)
                    .clickable {
                        // Zur vorherigen Seite navigieren (innerhalb einer Coroutine)
                        coroutineScope.launch {
                            val previousPage =
                                (pagerState.currentPage - 1 + menuItems.size) % menuItems.size
=======
                    .padding(start = 32.dp)
                    .size(60.dp)
                    .clickable {
                        coroutineScope.launch {
                            val previousPage = if (pagerState.currentPage - 1 < 0) menuItems.size - 1 else pagerState.currentPage - 1
>>>>>>> main
                            pagerState.animateScrollToPage(previousPage)
                        }
                    }
            )
        }
    }
}

@Composable
<<<<<<< HEAD
fun MenuItem(imageRes: Int, title: String, navController: NavHostController, packageName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                // Navigiere zum LoginScreen statt direkt zur App
                navController.navigate("login_screen") // Dies führt den Benutzer zum LoginScreen
            },
=======
fun MenuItem(imageRes: Int, title: String, navController: NavHostController, route: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { navController.navigate(route) },
>>>>>>> main
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

<<<<<<< HEAD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
=======
        // Halbtransparenter Hintergrund für besseren Kontrast
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.6f))
>>>>>>> main
                .align(Alignment.TopCenter)
                .padding(8.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
<<<<<<< HEAD
                fontSize = 70.sp,
=======
                fontSize = 80.sp,
>>>>>>> main
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
