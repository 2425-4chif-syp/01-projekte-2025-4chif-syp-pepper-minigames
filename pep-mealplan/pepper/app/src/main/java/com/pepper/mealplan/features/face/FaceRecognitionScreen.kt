package com.pepper.mealplan.features.face

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pepper.mealplan.RoboterActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun FaceRecognitionScreen(
    onAuthenticationSuccess: (String) -> Unit,
    viewModel: FaceRecognitionViewModel = viewModel()
) {
    // Dev-Schalter: auf true setzen, um Gesichtserkennung zu überspringen
    val devModeSkipFaceRecognition = true

    // State für Human Awareness Monitoring
    var isMonitoring by remember { mutableStateOf(true) }
    val isLoading by viewModel.isLoading
    val foundPerson by viewModel.foundPerson
    val errorMessage by viewModel.errorMessage
    val hasError by viewModel.hasError

    LaunchedEffect(Unit) {
        // Reset states when returning to the screen
        isMonitoring = true
        viewModel.clearError()
        viewModel.setOnAuthenticationSuccess {
            onAuthenticationSuccess(foundPerson)
        }
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
                        viewModel.takePicture()
                        isMonitoring = false
                    }
                }
            }
            delay(3000)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            isMonitoring = false
        }
    }

    val statusText = when {
        isLoading -> "Ich erkenne gerade das Gesicht."
        errorMessage != null -> "Bitte erneut versuchen."
        else -> "Bitte vor den Roboter stellen."
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
                    // DEV-SHORTCUT: Gesichtserkennung überspringen
                    if (devModeSkipFaceRecognition) {
                        onAuthenticationSuccess("Amir Mohamadi")
                        return@Button
                    }

                    if (errorMessage != null) {
                        viewModel.clearError()
                        isMonitoring = true
                        viewModel.takePicture()
                    }
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
                        ?: "Wenn nichts passiert, bitte einmal auf die große Kamera-Taste tippen.",
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
