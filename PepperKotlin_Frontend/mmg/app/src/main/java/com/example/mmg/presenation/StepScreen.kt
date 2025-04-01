package com.example.mmg.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mmg.viewmodel.StepScreenViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mmg.R

@Composable
fun StepScreen(
    viewModel: StepScreenViewModel = viewModel(),
    imageBase64: String?
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Anzeige des Bildes
        if(imageBase64 != null) {
            val imageBitmap = viewModel.base64ToBitmap(base64String = imageBase64)

            if(imageBitmap != null){
                Image(
                    bitmap = imageBitmap,
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
        }
        else {
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
                modifier = Modifier.width(150.dp).height(50.dp),
                onClick = {}
            ) {
                Text(text = "Abbrechen")
            }

            Button(
                modifier = Modifier.width(150.dp).height(50.dp),
                onClick = {}
            ) {
                Text(text = "Weiter")
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun StepScreenPreview() {
    val sampleImageBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA" +
            "AAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO" +
            "9TXL0Y4OHwAAAABJRU5ErkJggg==" // Beispiel-Bitmap

    StepScreen(
        imageBase64 = null  //sampleImageBase64,
    )
}