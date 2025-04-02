package com.example.mmg.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mmg.R
import com.example.mmg.viewmodel.MmgViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StepScreen(
    viewModel: MmgViewModel,
    navController: NavController
) {

    val imageBitmap by viewModel.imageBitMap.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Anzeige des Bildes
        if(imageBitmap != null){
            Image(
                bitmap = imageBitmap!!,
                contentDescription = "Step Picture",
                modifier = Modifier.size(400.dp)
            )
        }
        else{
            Image(
                painter = painterResource(id = R.drawable.default_step_picture),
                contentDescription = "Default Step Picture",
                modifier = Modifier.size(400.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp),
                onClick = {
                    viewModel.resetStepCount()
                    navController.popBackStack()
                }
            ) {
                Text(text = "Abbrechen")
            }

            Button(
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp),
                onClick = {
                    viewModel.displayStep()
                }
            ) {
                Text(text = "Weiter")
            }
        }
    }
}