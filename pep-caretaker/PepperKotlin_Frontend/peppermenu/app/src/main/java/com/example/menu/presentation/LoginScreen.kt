package com.example.menu.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.menu.RoboterActions
import com.example.menu.dto.Person
import com.example.menu.viewmodel.LoginScreenViewModel

@Composable
fun LoginScreen(
    onLoginClick: (Person) -> Unit,
    viewModel: LoginScreenViewModel
) {
    val selectedName by viewModel.selectedName
    val selectedPerson by viewModel.selectedPerson
    val isLoading by viewModel.isLoading
    val filteredPersons by viewModel.filteredPersons
    val searchQuery by viewModel.searchQuery
    val hasSelection = selectedPerson != null && selectedName.isNotBlank()

    val backgroundBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFF8EE),
                Color(0xFFF9F2FF),
                Color(0xFFEAF5F1)
            )
        )
    }
    val panelColor = Color(0xFFFFFCF7)
    val panelBorder = Color(0xFFE7DCCF)
    val mutedTextColor = Color(0xFF425466)

    LaunchedEffect(Unit) {
        RoboterActions.speak("Ich konnte dich nicht eindeutig erkennen. Bitte wähle jetzt deinen Namen aus der Liste.")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight(),
                color = panelColor,
                shape = RoundedCornerShape(28.dp),
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Anmeldung",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5E8B7E)
                    )

                    Text(
                        text = "Bitte Namen auswählen",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        color = Color(0xFF1F2937),
                        lineHeight = 36.sp
                    )

                    Text(
                        text = if (hasSelection) {
                            "Ausgewählt: $selectedName"
                        } else {
                            "Wähle links einen Namen aus oder versuche Gesichtserkennung erneut."
                        },
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        color = mutedTextColor
                    )

                    if (isLoading) {
                        Text(
                            text = "Ladevorgang läuft...",
                            fontSize = 16.sp,
                            color = Color(0xFF5C6B77),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            val person = selectedPerson ?: return@Button
                            Log.d("LoginScreen", "Manual login confirmed for pid=${person.pid}")
                            RoboterActions.speak("Danke. Ich bestätige jetzt deine Anmeldung.")
                            onLoginClick(person)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFBFE3C0),
                            contentColor = Color(0xFF14311F)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        enabled = !isLoading && hasSelection
                    ) {
                        Text(
                            text = "Anmeldung bestätigen",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.captureAndRecognizePerson()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(66.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFD8E9FF),
                            contentColor = Color(0xFF1A3A63)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = "Gesichtserkennung erneut",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxHeight(),
                color = panelColor,
                shape = RoundedCornerShape(28.dp),
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .background(Color(0xFFF7F1E8), RoundedCornerShape(24.dp))
                        .border(1.dp, panelBorder, RoundedCornerShape(24.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Namensliste",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF24323D)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::updateSearchQuery,
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            label = { Text("Name suchen") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF5E8B7E),
                                unfocusedBorderColor = Color(0xFFB7BFC8),
                                focusedLabelColor = Color(0xFF3C5C53)
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.applySearchFilter() },
                            modifier = Modifier
                                .height(56.dp)
                                .size(width = 120.dp, height = 56.dp),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF5E8B7E),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Suchen",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (filteredPersons.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Keine passenden Namen gefunden.",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF5B6773),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            userScrollEnabled = !isLoading
                        ) {
                            items(filteredPersons, key = { it.pid }) { person ->
                                val name = "${person.firstName} ${person.lastName}".trim()
                                Button(
                                    onClick = {
                                        viewModel.selectPerson(person)
                                        RoboterActions.speak("Ausgewählt: ${person.firstName} ${person.lastName}.")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (name == selectedName) {
                                            Color(0xFFDCEFD8)
                                        } else {
                                            Color.White
                                        },
                                        contentColor = Color(0xFF1F2937)
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    enabled = !isLoading
                                ) {
                                    Text(
                                        text = name,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
