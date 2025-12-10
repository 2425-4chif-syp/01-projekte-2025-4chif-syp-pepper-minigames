package com.pepper.mealplan.faceRecognition

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pepper.mealplan.RoboterActions
import kotlinx.coroutines.delay

@Composable
fun FaceRecognitionScreen(
    onAuthenticationSuccess: (String) -> Unit,
    viewModel: FaceRecognitionViewModel = viewModel()
) {
    // State für Human Awareness Monitoring
    var isMonitoring by remember { mutableStateOf(true) }
    val isLoading by viewModel.isLoading
    val foundPerson by viewModel.foundPerson

    // Set callback to pass foundPerson when authentication succeeds
    LaunchedEffect(Unit) {
        viewModel.setOnAuthenticationSuccess {
            onAuthenticationSuccess(foundPerson)
        }
    }

    LaunchedEffect(Unit) {
        delay(1000)
        viewModel.talkToPerson()
    }

    LaunchedEffect(isMonitoring) {
        while (isMonitoring) {
            viewModel.talkToPerson()
            delay(3000)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            isMonitoring = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Gesichtserkennung",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = {
                    onAuthenticationSuccess.invoke("Nikola Mladenovic")

                    // Development auskommtieren
                    /*
                    if (!isLoading) {
                        isMonitoring = false
                        viewModel.takePicture()
                    }
                    */
                },
                modifier = Modifier.size(180.dp),
                shape = MaterialTheme.shapes.large,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Kamera starten",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
    }
}