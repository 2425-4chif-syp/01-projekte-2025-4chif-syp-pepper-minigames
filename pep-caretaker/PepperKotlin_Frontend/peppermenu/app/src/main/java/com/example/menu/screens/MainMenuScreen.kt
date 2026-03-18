package com.example.menu.screens

import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.menu.PepperPhrases
import com.example.menu.R
import com.example.menu.RoboterActions
import com.example.menu.common.Packages
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainMenuScreen(
    personName: String?,
    onOpenApp: (String) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(personName) {
        Log.d("QiContext", "${RoboterActions.qiContext}")
        RoboterActions.speak(PepperPhrases.menuWelcome(personName))
    }

    val menuItems = listOf(
        Pair(R.drawable.essensplan, "Essensplan" to Packages.ESSENSPLAN),
        Pair(R.drawable.mitmachgeschichte, "Mitmachgeschichte" to Packages.MMG),
        Pair(R.drawable.memory_game, "Memory" to Packages.MEMORY_GAME),
        Pair(R.drawable.fang_den_dieb, "Fang den Dieb" to Packages.DIEBSPIEL),
        Pair(R.drawable.tic_tac_toe, "Tic Tac Toe" to Packages.TIC_TAC_TOE)
    )

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
            .background(Brush.horizontalGradient(colors = listOf(color1, color2)))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(0.78f)
                .fillMaxWidth()
        ) {
            HorizontalPager(
                count = menuItems.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                MenuItem(
                    imageRes = menuItems[page].first,
                    title = menuItems[page].second.first,
                    packageName = menuItems[page].second.second,
                    onOpenApp = onOpenApp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Swipe Right",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(90.dp)
                    .clickable {
                        coroutineScope.launch {
                            val nextPage = (pagerState.currentPage + 1) % menuItems.size
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }
            )

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Swipe Left",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(90.dp)
                    .clickable {
                        coroutineScope.launch {
                            val previousPage = (pagerState.currentPage - 1 + menuItems.size) % menuItems.size
                            pagerState.animateScrollToPage(previousPage)
                        }
                    }
            )
        }

        Row(
            modifier = Modifier
                .weight(0.22f)
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmallTalkButton(
                label = "Witz",
                modifier = Modifier.weight(1f),
                onClick = {
                    RoboterActions.speak(PepperPhrases.smallTalkJoke())
                }
            )
            SmallTalkButton(
                label = "Wissen",
                modifier = Modifier.weight(1f),
                onClick = {
                    RoboterActions.speak(PepperPhrases.smallTalkFact())
                }
            )
            SmallTalkButton(
                label = "Bewegung",
                modifier = Modifier.weight(1f),
                onClick = {
                    RoboterActions.speak(PepperPhrases.smallTalkMovement())
                }
            )
            SmallTalkButton(
                label = "Kompliment",
                modifier = Modifier.weight(1f),
                onClick = {
                    RoboterActions.speak(PepperPhrases.smallTalkCompliment(personName))
                }
            )
        }
    }
}

@Composable
private fun SmallTalkButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2F6DA7),
            contentColor = Color.White
        )
    ) {
        Text(
            text = label,
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun MenuItem(
    imageRes: Int,
    title: String,
    packageName: String,
    onOpenApp: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                RoboterActions.speak(PepperPhrases.launchingApp(title))
                onOpenApp(packageName)
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
