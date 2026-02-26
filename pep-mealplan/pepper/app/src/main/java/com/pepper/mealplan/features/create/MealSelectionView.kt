package com.pepper.mealplan.features.create

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pepper.mealplan.RoboterActions
import com.pepper.mealplan.data.menu.MenuRepository
import com.pepper.mealplan.network.RetrofitClient
import com.pepper.mealplan.network.dto.ApiMealPlanDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class FoodOptionUi(
    val foodId: Int,
    val name: String,
    val pictureId: Int?,
)

@Composable
fun MealSelectionView(
    weekNumber: Int,
    dayShort: String,
    mealStep: MealStep,               // nur MAIN oder EVENING
    onBackClick: () -> Unit,
    onMealSelected: (Int) -> Unit,
    dayLabel: String
) {
    val menuRepository = remember { MenuRepository() }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var options by remember { mutableStateOf<List<FoodOptionUi>>(emptyList()) }

    val weekDayIndex = remember(dayShort) { dayShortToIndex(dayShort) }
    val title = if (mealStep == MealStep.MAIN) "Mittagessen auswählen" else "Abendessen auswählen"

    // Pepper spricht freundlich
    LaunchedEffect(dayShort, mealStep) {
        val dayText = when (dayShort) {
            "MO" -> "Montag"
            "DI" -> "Dienstag"
            "MI" -> "Mittwoch"
            "DO" -> "Donnerstag"
            "FR" -> "Freitag"
            "SA" -> "Samstag"
            "SO" -> "Sonntag"
            else -> "heute"
        }
        val mealText = if (mealStep == MealStep.MAIN) "Mittagessen" else "Abendessen"
        RoboterActions.speak("Alles klar. Für $dayLabel kannst du jetzt dein $mealText auswählen. Tippe bitte auf eine der beiden Speisen.")
    }

    LaunchedEffect(weekNumber, weekDayIndex, mealStep) {
        isLoading = true
        error = null
        options = emptyList()

        val res = menuRepository.getMenuForDate(weekNumber, weekDayIndex)
        res.onSuccess { menu ->
            options = buildOptions(menu, mealStep)
            if (options.size < 2) {
                error = "Für diesen Tag sind nicht genug Speisen vorhanden."
            }
        }.onFailure { e ->
            error = e.message ?: "Fehler beim Laden des Menüs"
        }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                RoboterActions.stopSpeaking()
                onBackClick()
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Zurück")
            }

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Text(
                text = error!!,
                color = Color.Red,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    RoboterActions.stopSpeaking()
                    onBackClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Text("Zurück", color = Color.White, fontWeight = FontWeight.Bold)
            }
        } else {
            // 2 Optionen nebeneinander
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FoodOptionCard(
                    option = options[0],
                    modifier = Modifier.weight(1f),
                    onClick = { onMealSelected(options[0].foodId) }
                )
                FoodOptionCard(
                    option = options[1],
                    modifier = Modifier.weight(1f),
                    onClick = { onMealSelected(options[1].foodId) }
                )
            }
        }
    }
}

@Composable
private fun FoodOptionCard(
    option: FoodOptionUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(300.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BackendImage(
                pictureId = option.pictureId,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Text(
                text = option.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BackendImage(
    pictureId: Int?,
    modifier: Modifier = Modifier
) {
    var bmp by remember { mutableStateOf<ImageBitmap?>(null) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(pictureId) {
        bmp = null
        if (pictureId == null) return@LaunchedEffect

        loading = true
        val bytes = withContext(Dispatchers.IO) { RetrofitClient.fetchImage(pictureId) }
        if (bytes != null) {
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            bmp = bitmap?.asImageBitmap()
        }
        loading = false
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (bmp != null) {
            Image(
                bitmap = bmp!!,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (loading) {
            CircularProgressIndicator()
        } else {
            Text("Kein Bild")
        }
    }
}

private fun buildOptions(menu: ApiMealPlanDto, mealStep: MealStep): List<FoodOptionUi> {
    fun option(food: com.pepper.mealplan.network.dto.ApiFoodDto?): FoodOptionUi? {
        val id = food?.id ?: return null
        val name = food.name ?: return null
        val picId = food.picture?.id
        return FoodOptionUi(foodId = id, name = name, pictureId = picId)
    }

    return when (mealStep) {
        MealStep.MAIN -> listOfNotNull(option(menu.lunch1), option(menu.lunch2))
        MealStep.EVENING -> listOfNotNull(option(menu.dinner1), option(menu.dinner2))
    }
}

private fun dayShortToIndex(dayShort: String): Int = when (dayShort) {
    "MO" -> 0
    "DI" -> 1
    "MI" -> 2
    "DO" -> 3
    "FR" -> 4
    "SA" -> 5
    "SO" -> 6
    else -> 0
}
