package com.pepper.mealplan.MealPlanOverview

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pepper.mealplan.R
import com.pepper.mealplan.network.dto.FoodDto
import com.pepper.mealplan.network.dto.MenuDto

@Composable
fun MealPlanOverview(viewModel: MealPlanOverviewViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header mit Wochennavigation
        WeekNavigationHeader(
            currentWeek = viewModel.currentWeek,
            onPreviousWeek = viewModel::navigateToPreviousWeek,
            onNextWeek = viewModel::navigateToNextWeek
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Zeige Spinner während dem Laden
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colors.primary
                )
            }
        } else {
            // Wochenplan mit optimierter LazyColumn
            val weekMenus = remember(viewModel.currentWeek) { viewModel.weekMenus }
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = weekMenus,
                    key = { menu -> "${menu.weekNumber}_${menu.weekday}" }
                ) { menu ->
                    DayMenuCard(
                        menu = menu,
                        getFoodById = viewModel::getFoodById
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekNavigationHeader(
    currentWeek: Int,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousWeek,
            enabled = currentWeek > 1
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Vorherige Woche",
                tint = if (currentWeek > 1) MaterialTheme.colors.primary else Color.Gray
            )
        }
        
        Text(
            text = "Woche $currentWeek",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        IconButton(
            onClick = onNextWeek,
            enabled = currentWeek < 6
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Nächste Woche",
                tint = if (currentWeek < 6) MaterialTheme.colors.primary else Color.Gray
            )
        }
    }
}

@Composable
private fun DayMenuCard(
    menu: MenuDto,
    getFoodById: (Int?) -> com.pepper.mealplan.network.dto.FoodDto?
) {
    // Cache food lookups für bessere Performance
    val soupName = remember(menu.soupId) { getFoodById(menu.soupId)?.name ?: "Keine Angabe" }
    val m1Name = remember(menu.m1Id) { getFoodById(menu.m1Id)?.name ?: "Keine Angabe" }
    val m2Name = remember(menu.m2Id) { getFoodById(menu.m2Id)?.name ?: "Keine Angabe" }
    val dessertName = remember(menu.lunchDessertId) { getFoodById(menu.lunchDessertId)?.name ?: "Keine Angabe" }
    val a1Name = remember(menu.a1Id) { getFoodById(menu.a1Id)?.name ?: "Keine Angabe" }
    val a2Name = remember(menu.a2Id) { getFoodById(menu.a2Id)?.name ?: "Keine Angabe" }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Wochentag Header
            Text(
                text = getGermanWeekday(menu.weekday),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Suppe
            MenuSection(
                title = "Suppe",
                foodName = soupName,
                backgroundColor = Color(0xFFE3F2FD),
                foodType = "soup"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Hauptgerichte
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MenuSection(
                    title = "Hauptgericht 1",
                    foodName = m1Name,
                    backgroundColor = Color(0xFFF3E5F5),
                    foodType = "main",
                    modifier = Modifier.weight(1f)
                )
                
                MenuSection(
                    title = "Hauptgericht 2",
                    foodName = m2Name,
                    backgroundColor = Color(0xFFF3E5F5),
                    foodType = "main",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Dessert
            MenuSection(
                title = "Dessert",
                foodName = dessertName,
                backgroundColor = Color(0xFFFFF3E0),
                foodType = "dessert"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Abendessen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MenuSection(
                    title = "Abendessen 1",
                    foodName = a1Name,
                    backgroundColor = Color(0xFFE8F5E8),
                    foodType = "main",
                    modifier = Modifier.weight(1f)
                )
                
                MenuSection(
                    title = "Abendessen 2",
                    foodName = a2Name,
                    backgroundColor = Color(0xFFE8F5E8),
                    foodType = "main",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MenuSection(
    title: String,
    foodName: String,
    backgroundColor: Color,
    foodType: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bild anzeigen basierend auf dem Gerichtstyp
        if (foodType != null) {
            val context = LocalContext.current
            val bitmap = remember(foodType) {
                val imageRes = when (foodType) {
                    "soup" -> R.drawable.soup
                    "main" -> R.drawable.main
                    "dessert" -> R.drawable.dessert
                    else -> R.drawable.main
                }
                
                // Lade das Bild mit reduzierten Optionen
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 4 // Reduziert die Bildgröße um Faktor 4
                    inPreferredConfig = android.graphics.Bitmap.Config.RGB_565 // Weniger Speicherverbrauch
                }
                
                try {
                    val inputStream = context.resources.openRawResource(imageRes)
                    val bmp = BitmapFactory.decodeStream(inputStream, null, options)
                    inputStream.close()
                    bmp?.asImageBitmap()
                } catch (e: Exception) {
                    null
                }
            }
            
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "$title Bild",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback: Verwende einen einfachen farbigen Platzhalter
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (foodType) {
                                "soup" -> Color(0xFF2196F3)
                                "main" -> Color(0xFF4CAF50)
                                "dessert" -> Color(0xFFFF9800)
                                else -> Color.Gray
                            }
                        )
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = foodName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

private fun getGermanWeekday(weekday: String): String {
    return when (weekday) {
        "MO" -> "Montag"
        "DI" -> "Dienstag"
        "MI" -> "Mittwoch"
        "DO" -> "Donnerstag"
        "FR" -> "Freitag"
        "SA" -> "Samstag"
        "SO" -> "Sonntag"
        else -> weekday
    }
}