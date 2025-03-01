package com.example.smartloginapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onLoginClick: () -> Unit, onContinueWithoutLogin: () -> Unit) {
    var selectedName by remember { mutableStateOf("Hermine Mayer") }
    var expanded by remember { mutableStateOf(false) }
    val names = listOf("Hermine Mayer", "Max Mustermann", "Anna Müller", "John Doe")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Text "Sind sie Frau Hermine Mayer?" oben zentriert und etwas weiter oben als Standard
        Text(
            text = "Sind sie Frau Hermine Mayer?",
            fontSize = 60.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp), // Etwas nach oben verschieben
            color = Color.Black,
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Zwei Buttons nebeneinander
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .weight(0.5f)
                    .height(90.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Ja",
                    fontSize = 30.sp
                )
            }

            Button(
                onClick = onContinueWithoutLogin,
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Ohne Anmeldung weiter",
                    fontSize = 30.sp
                )
            }
        }

        // Ab hier kommen die weiteren UI-Elemente (Dropdown, Icons etc.)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        onLoginClick = {},
        onContinueWithoutLogin = {}
    )
}
