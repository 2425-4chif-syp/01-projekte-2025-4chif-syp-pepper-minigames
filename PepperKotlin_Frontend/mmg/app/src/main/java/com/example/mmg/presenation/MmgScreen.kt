package com.example.mmg.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mmg.viewmodel.MmgViewModel

@Composable
fun MmgScreen(viewModel: MmgViewModel = viewModel()) {
    val mmgList by viewModel.mmgList.collectAsState()  // State aus dem ViewModel beobachten

    LaunchedEffect(Unit) {
        viewModel.loadMmgDtos()  // Daten beim ersten Rendern laden
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Hello Pepper", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (mmgList.isEmpty()) {
            CircularProgressIndicator()  // Lade-Animation anzeigen
        } else {
            mmgList.forEach { mmg ->
                Text("ID: ${mmg.id}, Name: ${mmg.name}, Value: ${mmg.gameType.name}")
            }
        }
    }
}