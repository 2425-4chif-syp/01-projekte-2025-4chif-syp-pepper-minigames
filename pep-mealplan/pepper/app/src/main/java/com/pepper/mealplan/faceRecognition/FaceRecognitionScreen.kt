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
    var isMonitoring by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
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
                    // TODO: Implement face recognition logic
                    isMonitoring = false
                    onAuthenticationSuccess()
                },
                modifier = Modifier.size(180.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Kamera starten",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}