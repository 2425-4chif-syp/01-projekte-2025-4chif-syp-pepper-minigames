package com.example.menu.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.menu.R
import com.example.menu.RoboterActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun WakeWordActivationScreen(
    onWakeWordDetected: () -> Unit
) {
    var statusText by remember {
        mutableStateOf("Sage \"Hallo Pepper\", und wir starten gemeinsam.")
    }

    LaunchedEffect(Unit) {
        delay(700)
        RoboterActions.speak("Sage Hallo Pepper, um mich zu aktivieren.")
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (!RoboterActions.robotExecute || RoboterActions.qiContext == null) {
                statusText = "Ich verbinde mich kurz mit Pepper."
                delay(700)
                continue
            }

            statusText = "Ich höre zu. Codewort: Hallo Pepper"
            val wasActivated = withContext(Dispatchers.IO) {
                RoboterActions.waitForWakeWord()
            }

            if (wasActivated) {
                statusText = "Super, wir starten die Gesichtserkennung."
                RoboterActions.speak("Super, ich starte jetzt die Gesichtserkennung.")
                delay(500)
                onWakeWordDetected()
                return@LaunchedEffect
            }

            statusText = "Ich habe dich nicht verstanden. Sag bitte Hallo Pepper."
            delay(500)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF1F7FF), Color(0xFFF6FFF4))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.htl_leonding_logo),
                contentDescription = "HTL Leonding Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentScale = ContentScale.Fit
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Sage Hallo Pepper",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF0B3A53)
                    )
                    Text(
                        text = "Dann aktiviere ich mich und starte mit der Gesichtserkennung.",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1E4256)
                    )
                    CircularProgressIndicator(color = Color(0xFF0B6FB5))
                    Text(
                        text = statusText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1F2937)
                    )
                }
            }
        }
    }
}
