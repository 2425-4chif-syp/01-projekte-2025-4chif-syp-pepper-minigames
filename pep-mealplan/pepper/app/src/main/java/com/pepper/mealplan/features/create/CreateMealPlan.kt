package com.pepper.mealplan.features.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CreateMealPlan(
    foundPerson: String = "",
    onBackToMenu: () -> Unit,
    onOrderSuccess: () -> Unit = {},
    vm: CreateMealPlanViewModel = viewModel(
        key = "create_meal_plan_$foundPerson",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreateMealPlanViewModel(foundPerson) as T
            }
        }
    )
) {
    // Wenn fertig -> Menü
    LaunchedEffect(vm.navigateToMenu) {
        if (vm.navigateToMenu) onBackToMenu()
    }

    LaunchedEffect(vm.successfulOrderVersion) {
        if (vm.successfulOrderVersion > 0) {
            onOrderSuccess()
        }
    }

    when (vm.stage) {
        CreateStage.DAY_PICK -> {
            DaysPickView(
                days = vm.pendingDays,
                onDayClick = { vm.onDayClicked(it) },
                onBackClick = { vm.onBackPressedToMenu() }
            )
        }

        CreateStage.MEALTYPE_PICK -> {
            MealTypePickView(
                dayLabel = vm.selectedDay?.label ?: "",
                dateText = vm.selectedDay?.displayDate ?: "",
                onPickLunch = { vm.onMealTypeClicked(MissingMealType.LUNCH) },
                onPickDinner = { vm.onMealTypeClicked(MissingMealType.DINNER) },
                onBack = { vm.backFromMealTypePicker() }
            )
        }

        CreateStage.MEAL_SELECTION -> {
            val day = vm.selectedDay
            if (day != null) {
                // Wichtig: day.label für Pepper (Heute/Morgen/Übermorgen)
                MealSelectionView(
                    weekNumber = day.weekNumber,
                    dayShort = day.dayShort,
                    mealStep = vm.currentMealStep,
                    dayLabel = day.label,            // ✅ brauchst du in MealSelectionView (siehe unten)
                    onBackClick = { vm.onSelectionBack() },
                    onMealSelected = { foodId, foodName -> vm.onFoodChosen(foodId, foodName) }
                )

                vm.errorMessage?.let { msg ->
                    // optional: du kannst später einen Snackbar/Overlay machen
                    // aktuell lassen wir es still, weil UI sonst unruhig wird
                }
            } else {
                // Fallback
                DaysPickView(
                    days = vm.pendingDays,
                    onDayClick = { vm.onDayClicked(it) },
                    onBackClick = { vm.onBackPressedToMenu() }
                )
            }
        }
    }
}

@Composable
private fun DaysPickView(
    days: List<NextDayUi>,
    onDayClick: (NextDayUi) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bestellung",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        if (days.isEmpty()) {
            // Wenn keine Tage -> sollte eigentlich direkt ins Menü navigieren
            Text("Es gibt nichts mehr zu bestellen.")
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Zurück", color = Color.White, fontWeight = FontWeight.Bold)
            }
        } else {
            // 2x2 Grid: bis zu 3 Tage + Zurück
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DayBox(
                    title = days.getOrNull(0)?.label ?: "",
                    dateText = days.getOrNull(0)?.displayDate ?: "",
                    modifier = Modifier.weight(1f).height(160.dp),
                    onClick = { days.getOrNull(0)?.let(onDayClick) }
                )
                DayBox(
                    title = days.getOrNull(1)?.label ?: "",
                    dateText = days.getOrNull(1)?.displayDate ?: "",
                    modifier = Modifier.weight(1f).height(160.dp),
                    onClick = { days.getOrNull(1)?.let(onDayClick) }
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DayBox(
                    title = days.getOrNull(2)?.label ?: "",
                    dateText = days.getOrNull(2)?.displayDate ?: "",
                    modifier = Modifier.weight(1f).height(160.dp),
                    onClick = { days.getOrNull(2)?.let(onDayClick) }
                )

                BackRedBox(
                    modifier = Modifier.weight(1f).height(160.dp),
                    onClick = onBackClick
                )
            }
        }
    }
}

@Composable
private fun DayBox(
    title: String,
    dateText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BackRedBox(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Zurück",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun MealTypePickView(
    dayLabel: String,
    dateText: String,
    onPickLunch: () -> Unit,
    onPickDinner: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$dayLabel – $dateText",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(
                modifier = Modifier.weight(1f).height(190.dp).clickable { onPickLunch() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Mittagessen bestellen",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f).height(190.dp).clickable { onPickDinner() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Abendessen bestellen",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Zurück", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
