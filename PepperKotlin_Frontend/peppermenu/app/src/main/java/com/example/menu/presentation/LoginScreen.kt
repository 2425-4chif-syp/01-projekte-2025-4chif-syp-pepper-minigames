package com.example.menu.presentation


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.menu.RoboterActions
import com.example.menu.viewmodel.LoginScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onContinueWithoutLogin: () -> Unit,
    navController: NavHostController,
    viewModel: LoginScreenViewModel = viewModel()
) {
    val selectedName by viewModel.selectedName
    val context = LocalContext.current
    val permissionGranted = remember { mutableStateOf(false) }

    RequestAudioPermission {
        permissionGranted.value = true
        Log.d("LoginScreen", "Audio-Berechtigung wurde erteilt.")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 50.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "Sind sie Frau ${selectedName}?", // Dynamischer Text basierend auf selectedName
                fontSize = 60.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
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
                        .height(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF4CAF50), // Grün
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Ja",
                        fontSize = 50.sp
                    )
                }
                Button(
                    onClick = onContinueWithoutLogin,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF2196F3), // Blau
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Ohne Anmeldung weiter",
                        fontSize = 50.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Row für ScrollView und Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ScrollView für Namensauswahl
                Column(
                    modifier = Modifier
                        .width(400.dp)
                        .height(400.dp)
                        .background(Color(0xFFE0E0E0)) // Hellgrau
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Text "Wählen Sie Ihren Namen aus"
                    Text(
                        text = "Wählen Sie Ihren Namen aus",
                        fontSize = 26.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // LazyColumn für die Namensliste mit Scrollbar
                    val scrollState = rememberLazyListState()
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(viewModel.names.size) { index ->
                            Button(
                                onClick = {
                                    viewModel.setName(viewModel.names[index])
                                }, // Aktualisiere selectedName
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (viewModel.names[index] == selectedName) Color(
                                        0xFFFFEB3B
                                    ) else Color.White, // Gelb für Auswahl
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(text = viewModel.names[index], fontSize = 30.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp)) // Abstand zwischen den Buttons
                        }
                    }
                }

                // Column für Icons und Text
                Column(
                    modifier = Modifier
                        .padding(start = 50.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Gesichtserkennung
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFE0E0E0)) // Hellgrau
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                //viewModel.testConnection()
                                viewModel.captureAndRecognizePerson()
                            },
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Gesichtserkennung",
                                modifier = Modifier.fillMaxSize(),
                                tint = Color(0xFFFFA500) // Orange
                            )
                        }
                        Text(
                            text = "Gesichtserkennung",
                            fontSize = 30.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(1.dp))

                    // Spracherkennung
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFE0E0E0)) // Hellgrau
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { viewModel.startSpeechRecognition() },
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Spracherkennung",
                                modifier = Modifier.fillMaxSize(),
                                tint = Color(0xFF4CAF50) // Grün
                            )
                        }
                        Text(
                            text = "Spracherkennung",
                            fontSize = 30.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }

        // Kleiner Button unten rechts
        Button(
            onClick = {
                navController.navigate("main_menu") // Navigiere zurück zum Hauptmenü
            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // Positioniere den Button unten rechts
                .padding(16.dp) // Abstand vom Rand
                .width(100.dp) // Breite des Buttons
                .height(50.dp), // Höhe des Buttons
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFF44336), // Rot
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Zurück",
                fontSize = 16.sp // Kleinere Schriftgröße
            )
        }
    }
}

@Composable
fun RequestAudioPermission(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Mikrofon-Berechtigung erforderlich", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            onPermissionGranted()
        }
    }
}
