package com.example.smartloginapp.presentation

import androidx.compose.foundation.background
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

        Spacer(modifier = Modifier.height(32.dp))

        // Alles in einer Row packen (Dropdown + Gesichts- und Spracherkennung Icons)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dropdown für Namensauswahl
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.width(300.dp) // Breite des Textfeldes anpassen
            ) {
                TextField(
                    value = selectedName,
                    onValueChange = {},
                    label = { Text("Wählen Sie Ihren Namen") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.h5.copy(fontSize = 18.sp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .heightIn(min = 150.dp)
                        .background(Color.White)
                ) {
                    names.forEach { name ->
                        DropdownMenuItem(
                            onClick = {
                                selectedName = name
                                expanded = false
                            },
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text(text = name, fontSize = 18.sp)
                        }
                    }
                }
            }

            // Icons für Gesichts- und Spracherkennung
            IconButton(onClick = { /* Handle Gesichtserkennung */ }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Gesichtserkennung",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFFFFA500) // Orange
                )
            }

            IconButton(onClick = { /* Handle Spracherkennung */ }) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Spracherkennung",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Green
                )
            }
        }
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
