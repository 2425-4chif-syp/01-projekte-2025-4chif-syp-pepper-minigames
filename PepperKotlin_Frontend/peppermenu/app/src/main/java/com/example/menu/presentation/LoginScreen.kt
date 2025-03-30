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
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    val selectedGender by viewModel.selectedGender
    val isLoading by viewModel.isLoading
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
                // Dynamischer Text basierend auf selectedName
                text = "Sind sie ${selectedGender} ${selectedName}?",
                fontSize = 65.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
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
                        backgroundColor = Color.Green,
                        contentColor = Color.Black,
                    ),
                    enabled = !isLoading

                ) {
                    Text(
                        text = "Ja",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = onContinueWithoutLogin,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.Black
                    ),
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Ohne Anmeldung weiter",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Row für ScrollView und Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // ScrollView für Namensauswahl
                Column(
                    modifier = Modifier
                        .width(400.dp)
                        .height(500.dp)
                        .background(Color.LightGray)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Text "Wählen Sie Ihren Namen aus"
                    Text(
                        text = "Wählen Sie Ihren Namen aus ⬇️",
                        fontSize = 26.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp),
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // LazyColumn für die Namensliste mit Scrollbar
                    val scrollState = rememberLazyListState()
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = !isLoading
                    ) {
                        items(viewModel.names.value.size) { index ->
                            Button(
                                onClick = {
                                    viewModel.setName(viewModel.names.value[index])
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)
                                    .padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor =
                                    if (viewModel.names.value[index] == selectedName) Color.Green else Color.White,
                                    contentColor = Color.Black
                                ),
                               enabled = !isLoading
                            ) {
                                Text(
                                    text = viewModel.names.value[index],
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(start = 36.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    // Gesichtserkennung
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.LightGray)
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable(
                                enabled = !isLoading
                            ){
                                viewModel.captureAndRecognizePerson()
                            }
                    ) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp),
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Gesichtserkennung",
                                modifier = Modifier.fillMaxSize(),
                                tint = if (!isLoading) Color.Blue else Color.Gray
                            )
                        }
                        Text(
                            text = "Gesichtserkennung",
                            fontSize = 30.sp,
                            modifier = Modifier.padding(start = 16.dp),
                            /*color = if (viewModel.isLoading.value == true) Color.Gray else Color.Black // Grau während des Ladens */
                        )
                    }

                    Spacer(modifier = Modifier.height(1.dp))

                    // Spracherkennung
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFE0E0E0))
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable(
                                enabled = !isLoading
                            ){
                                viewModel.startSpeechRecognition()
                            }
                    ) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp),
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Spracherkennung",
                                modifier = Modifier.fillMaxSize(),
                                tint = if(!isLoading) Color.Blue else Color.Gray
                            )
                        }
                        Text(
                            text = "Spracherkennung",
                            fontSize = 30.sp,
                            modifier = Modifier.padding(start = 16.dp),
                             color = if (viewModel.isLoading.value == true) Color.Gray else Color.Black // Grau während des Ladens */
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                navController.navigate("main_menu")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 0.dp, end = 2.dp)
                .width(120.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFF44336), // Rot
                contentColor = Color.Black,
            ),
            enabled = !isLoading
        ) {
            Text(
                text = "Zurück",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
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
            Toast.makeText(context, "Mikrofon-Berechtigung erforderlich", Toast.LENGTH_SHORT)
                .show()
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