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
import kotlinx.coroutines.delay

@Composable
fun FaceRecognitionScreen(
    onAuthenticationSuccess: () -> Unit = {},
    viewModel: FaceRecognitionViewModel = viewModel()
){
    // State für Human Awareness Monitoring
    var isMonitoring by remember { mutableStateOf(true) }
    val isLoading by viewModel.isLoading
    val foundPerson by viewModel.foundPerson

    // Setze das Callback im ViewModel
    LaunchedEffect(onAuthenticationSuccess) {
        viewModel.setOnAuthenticationSuccess(onAuthenticationSuccess)
    }

    // LaunchedEffect für das initiale Aufrufen beim Laden der Seite
    LaunchedEffect(Unit) {
        viewModel.talkToPerson()
    }

    // LaunchedEffect für kontinuierliches Monitoring der Human Awareness
    LaunchedEffect(isMonitoring) {
        while (isMonitoring) {
            viewModel.talkToPerson()
            delay(3000) // Prüfe alle 3 Sekunden
        }
    }

    // Stoppe das Monitoring wenn die Composable verlassen wird
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
                    if (!isLoading) {
                        isMonitoring = false // Stoppe das Monitoring beim Foto machen
                        viewModel.takePicture()
                        // Remove the incorrect callback check - let the ViewModel handle success
                    }
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