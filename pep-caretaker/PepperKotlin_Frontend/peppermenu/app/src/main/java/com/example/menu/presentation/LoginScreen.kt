package com.example.menu.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = panelColor,
                shape = RoundedCornerShape(28.dp),
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Anmeldung",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5E8B7E)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Bitte Namen auswählen",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1F2937),
                        lineHeight = 38.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (hasSelection) {
                            "Ausgewählt: $selectedName"
                        } else {
                            "Tippe bitte auf deinen Namen in der Liste."
                        },
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center,
                        color = mutedTextColor,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

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
                            contentColor = Color(0xFF14311F),
                        ),
                        shape = RoundedCornerShape(18.dp),
                        enabled = !isLoading && hasSelection
                    ) {
                        Text(
                            text = "Anmeldung bestätigen",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(510.dp)
                            .background(Color(0xFFF7F1E8), RoundedCornerShape(24.dp))
                            .border(1.dp, panelBorder, RoundedCornerShape(24.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Namensliste",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF24323D)
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            userScrollEnabled = !isLoading
                        ) {
                            itemsIndexed(viewModel.names.value) { index, name ->
                                val person = viewModel.persons?.getOrNull(index)
                                Button(
                                    onClick = {
                                        if (person != null) {
                                            viewModel.selectPerson(person)
                                            RoboterActions.speak("Ausgewählt: ${person.firstName} ${person.lastName}.")
                                        }
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
                                    enabled = !isLoading && person != null
                                ) {
                                    Text(
                                        text = name,
                                        fontSize = 26.sp,
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
