package com.pepper.mealplan.MealPlanOverview

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pepper.mealplan.RoboterActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun OrderReminderScreen(
    foundPerson: String,
    onGoToOrder: () -> Unit,
    onGoToOverview: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // Text für Anzeige und Sprache
    val message = remember(foundPerson) {
        buildReminderText(foundPerson)
    }

    // Pepper spricht automatisch, wenn der Screen geöffnet wird
    LaunchedEffect(foundPerson) {
        scope.launch(Dispatchers.IO) {
            RoboterActions.speak(message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Überschrift + Hinweistext
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Essensbestellung überprüfen",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        // Zwei große Buttons unten
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // BLAUER Button – Bestellen
            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        RoboterActions.speak("Wir gehen jetzt zur Bestellung.")
                    }
                    onGoToOrder()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF2196F3) // blau
                )
            ) {
                Text(
                    text = "Bestellen",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // GRÜNER Button – Menü nur anzeigen
            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        RoboterActions.speak("Ich zeige dir jetzt den Essensplan.")
                    }
                    onGoToOverview()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4CAF50) // grün
                )
            ) {
                Text(
                    text = "Menü anzeigen",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Text, den Pepper sagt und der am Screen steht
private fun buildReminderText(foundPerson: String): String {
    val namePart = if (foundPerson.isNotBlank()) {
        "$foundPerson, "
    } else {
        ""
    }

    // Hier kannst du später echte Logik einbauen (konkreter Tag/Mahlzeit)
    return namePart +
            "für die nächsten drei Tage sind noch nicht alle Mahlzeiten bestellt. " +
            "Wenn du mit der Bestellung fortfahren möchtest, drücke bitte auf den blauen Button. " +
            "Wenn du nur den Essensplan ansehen möchtest, drücke auf den grünen Button."
}
