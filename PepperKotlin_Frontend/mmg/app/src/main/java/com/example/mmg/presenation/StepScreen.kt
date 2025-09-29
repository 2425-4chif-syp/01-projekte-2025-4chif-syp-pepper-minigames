package com.example.mmg.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mmg.R
import com.example.mmg.viewmodel.MmgViewModel

@Composable
fun StepScreen(
    viewModel: MmgViewModel,
    navController: NavController
) {

    val imageBitmap by viewModel.imageBitMap.collectAsState()
    val mmgSteps by viewModel.mmgStep.collectAsState()
    val stepsFinished by viewModel.stepsFinished.collectAsState()

    if(mmgSteps.isEmpty())
    {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    else
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = "Step Picture",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_step_picture),
                        contentDescription = "Default Step Picture",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
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

                        if(stepsFinished){
                            navController.popBackStack()
                        }
                        else{
                            viewModel.displayStep()
                        }
                    }
                ) {
                    Text(text = "Weiter")
                }
            }
        }

    }
}