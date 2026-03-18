package com.example.menu.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.menu.RoboterActions
import com.example.menu.dto.Person
import com.example.menu.viewmodel.InitialFaceRecognitionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun InitialFaceRecognitionScreen(
    onAuthenticationSuccess: (Person) -> Unit,
    onManualSelectionRequired: () -> Unit,
    viewModel: InitialFaceRecognitionViewModel = viewModel()
) {
    // Dev-Schalter: auf true setzen, um Gesichtserkennung zu ueberspringen
    val devModeSkipFaceRecognition = true

    var isMonitoring by remember { mutableStateOf(true) }
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val hasError by viewModel.hasError
    val requiresManualSelection by viewModel.requiresManualSelection

    LaunchedEffect(Unit) {
        isMonitoring = true
        viewModel.clearError()
    }

    LaunchedEffect(Unit) {
        delay(1000)
        viewModel.talkToPerson()
    }

    LaunchedEffect(isMonitoring, hasError) {
        while (isMonitoring && !hasError) {
            withContext(Dispatchers.IO) {
                val humanAwareness = RoboterActions.getHumanAwarness()
                if (humanAwareness != null) {
                    withContext(Dispatchers.Main) {
                        viewModel.talkToPerson()
                        viewModel.takePicture(onAuthenticationSuccess)
                        isMonitoring = false
                    }
                }
            }
            delay(3000)
        }
    }

    LaunchedEffect(requiresManualSelection) {
        if (requiresManualSelection) {
            delay(500)
            onManualSelectionRequired()
            viewModel.clearError()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            isMonitoring = false
        }
    }

    val statusText = when {
        isLoading -> "Ich schaue kurz, ob ich dich erkenne."
        errorMessage != null -> "Lass uns das nochmal versuchen."
        else -> "Stell dich bitte kurz vor mich."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF2F8FF), Color(0xFFEAF6F1))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Gesichtserkennung",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = statusText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF294861),
                        textAlign = TextAlign.Center
                    )
                    if (devModeSkipFaceRecognition) {
                        Text(
                            text = "Testmodus aktiv",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8A3E00)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Button(
                onClick = {
                    if (devModeSkipFaceRecognition) {
                        viewModel.skipFaceRecognitionForDevMode(
                            onAuthenticationSuccess = onAuthenticationSuccess,
                            onFallbackToManualSelection = onManualSelectionRequired
                        )
                        return@Button
                    }
                    onManualSelectionRequired()
                },
                modifier = Modifier.size(180.dp),
                shape = RoundedCornerShape(90.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (errorMessage != null) Color(0xFFEF5350) else MaterialTheme.colorScheme.primary
                ),
                enabled = !isLoading && (errorMessage != null || devModeSkipFaceRecognition)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(54.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Kamera starten",
                            modifier = Modifier.size(82.dp)
                        )
                        Text(
                            text = if (errorMessage != null) "Nochmal" else "Start",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Text(
                    text = errorMessage
                        ?: "Wenn nichts passiert, tipp bitte einmal auf die grosse Kamera-Taste.",
                    color = if (errorMessage != null) MaterialTheme.colorScheme.error else Color(0xFF1F2937),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                )
            }
        }
    }
}
