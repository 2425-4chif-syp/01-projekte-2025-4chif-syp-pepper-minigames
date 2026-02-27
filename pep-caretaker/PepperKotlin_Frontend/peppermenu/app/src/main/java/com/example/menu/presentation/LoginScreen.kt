package com.example.menu.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.menu.RoboterActions
import com.example.menu.viewmodel.LoginScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoginScreen(
    onLoginClick: (Long) -> Unit,
    onContinueWithoutLogin: () -> Unit,
    navController: NavHostController,
    viewModel: LoginScreenViewModel
) {
    val selectedName by viewModel.selectedName
    val isLoading by viewModel.isLoading
    var permissionGranted by remember { mutableStateOf(false) }

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
    val accentColor = Color(0xFF2D6A4F)
    val mutedTextColor = Color(0xFF425466)
    val selectedDisplayName = selectedName.takeIf { it.isNotBlank() } ?: "Noch nichts ausgewählt"

    RequestAudioPermission {
        permissionGranted = true
        Log.d("LoginScreen", "Audio-Berechtigung wurde erteilt.")
    }

    LaunchedEffect(Unit) {
        RoboterActions.speak(
            "Willkommen. Du kannst deinen Namen antippen, die Gesichtserkennung benutzen oder deinen Namen sagen."
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(horizontal = 30.dp, vertical = 20.dp)
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
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Willkommen",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5E8B7E)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Wie möchtest du dich anmelden?",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1F2937),
                        lineHeight = 34.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Wähle einfach die Möglichkeit, die für dich am angenehmsten ist.",
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        color = mutedTextColor,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                val id = viewModel.selectedPerson?.pid?.toLong() ?: -1L
                                Log.d("LoginScreen", "onLoginClick -> id=$id")
                                RoboterActions.speak("Alles klar. Ich bestätige jetzt deine Anmeldung.")
                                onLoginClick(id)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFBFE3C0),
                                contentColor = Color(0xFF14311F),
                            ),
                            shape = RoundedCornerShape(18.dp),
                            enabled = !isLoading
                        ) {
                            Text(
                                text = "Anmeldung bestätigen",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            onClick = {
                                RoboterActions.speak("Du kannst jetzt ohne Anmeldung weitermachen.")
                                onContinueWithoutLogin()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFF3D4C2),
                                contentColor = Color(0xFF4B2B18)
                            ),
                            shape = RoundedCornerShape(18.dp),
                            enabled = !isLoading
                        ) {
                            Text(
                                text = "Ohne Anmeldung weiter",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1.25f)
                                .height(440.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFFF7F1E8))
                                .border(1.dp, panelBorder, RoundedCornerShape(24.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Namen auswählen",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF24323D)
                            )

                            Text(
                                text = "Ausgewählt: $selectedDisplayName",
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = mutedTextColor
                            )

                            val scrollState = rememberLazyListState()
                            LazyColumn(
                                state = scrollState,
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                userScrollEnabled = !isLoading
                            ) {
                                items(viewModel.names.value.size) { index ->
                                    val name = viewModel.names.value[index]
                                    Button(
                                        onClick = {
                                            val p = viewModel.persons?.getOrNull(index)
                                            if (p != null) {
                                                viewModel.selectedPerson = p
                                                viewModel.setName("${p.firstName} ${p.lastName}")
                                                viewModel.setGender(p.gender)
                                                Log.d("LoginScreen", "Selected pid=${p.pid} name=${p.firstName} ${p.lastName}")
                                                RoboterActions.speak("Ausgewählt: ${p.firstName} ${p.lastName}. Wenn alles passt, drücke auf Anmeldung bestätigen.")
                                            } else {
                                                viewModel.setName(name)
                                                viewModel.findRightPerson(name)
                                                RoboterActions.speak("Ausgewählt: $name.")
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = if (name == selectedName) {
                                                Color(0xFFDCEFD8)
                                            } else {
                                                Color.White
                                            },
                                            contentColor = Color(0xFF1F2937)
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        enabled = !isLoading
                                    ) {
                                        Text(
                                            text = name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(440.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PepperActionButton(
                                title = "Gesichtserkennung starten",
                                subtitle = "Pepper erkennt dich über die Kamera.",
                                icon = Icons.Default.AccountCircle,
                                iconTint = accentColor,
                                enabled = !isLoading,
                                onClick = {
                                    RoboterActions.speak("Ich starte jetzt die Gesichtserkennung. Schau bitte kurz nach vorne.")
                                    viewModel.captureAndRecognizePerson()
                                }
                            )

                            PepperActionButton(
                                title = "Spracherkennung starten",
                                subtitle = "Sage deinen Namen langsam und deutlich.",
                                icon = Icons.Default.Mic,
                                iconTint = accentColor,
                                enabled = !isLoading && permissionGranted,
                                onClick = {
                                    RoboterActions.speak("Ich höre jetzt zu. Sage bitte deinen Namen.")
                                    viewModel.startSpeechRecognition()
                                }
                            )

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Button(
                                    onClick = { navController.navigate("main_menu") },
                                    modifier = Modifier
                                        .widthIn(min = 130.dp)
                                        .height(54.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(0xFFE9EDF5),
                                        contentColor = Color(0xFF24323D),
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    enabled = !isLoading
                                ) {
                                    Text(
                                        text = "Zurück",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
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

@Composable
private fun PepperActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        color = if (enabled) Color(0xFFF2F6F9) else Color(0xFFF1F1F1),
        shape = RoundedCornerShape(22.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (enabled) Color(0xFFDDEEE6) else Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    tint = if (enabled) iconTint else Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color(0xFF1F2937) else Color.Gray
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = if (enabled) Color(0xFF4B5563) else Color.Gray
                )
            }
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
