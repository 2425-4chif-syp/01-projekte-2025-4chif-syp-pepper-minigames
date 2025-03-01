package com.example.smartloginapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
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
import com.example.smartloginapp.ui.theme.SmartLoginAppTheme

@OptIn(ExperimentalMaterialApi::class)
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
                .padding(top = 60.dp), // Etwas nach oben verschieben
            color = Color.Black,
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Dropdown für Namensauswahl
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.width(450.dp) // Breite des Textfeldes anpassen
        ) {
            TextField(
                value = selectedName,
                onValueChange = {},
                label = { Text("Wählen Sie Ihren Namen ⬇️", fontSize = 30.sp) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Yellow, // Hintergrundfarbe des Textfeldes
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.h5.copy(fontSize = 40.sp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .heightIn(min = 150.dp) // Mindesthöhe des Dropdowns
                    .background(Color.White)
            ) {
                names.forEach { name ->
                    DropdownMenuItem(
                        onClick = {
                            selectedName = name
                            expanded = false
                        },
                        modifier = Modifier.height(60.dp)
                    ) {
                        Text(text = name, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    SmartLoginAppTheme {
        LoginScreen(
            onLoginClick = {},
            onContinueWithoutLogin = {}
        )
    }
}