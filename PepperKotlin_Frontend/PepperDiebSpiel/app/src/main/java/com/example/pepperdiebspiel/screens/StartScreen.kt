package com.example.pepperdiebspiel.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pepperdiebspiel.R

@Composable
fun StartScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6A5ACD), Color(0xFFB0C4DE))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.thief), // Eine schöne Illustration, die das Thema des Spiels visualisiert
                contentDescription = "Game Logo",
                modifier = Modifier
                    .size(250.dp)
                    .padding(bottom = 16.dp)
            )
            Text(
                text = "Fang den Dieb🥷",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.shadow(4.dp, RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    navController.navigate("game")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4682B4)),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(60.dp)
                    .width(200.dp)
                    .shadow(10.dp, RoundedCornerShape(30.dp))
            ) {
                Text(
                    text = "Start",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
