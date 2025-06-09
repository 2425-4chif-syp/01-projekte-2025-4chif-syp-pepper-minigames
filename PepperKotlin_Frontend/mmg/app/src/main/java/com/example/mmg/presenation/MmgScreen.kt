package com.example.mmg.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mmg.viewmodel.MmgViewModel
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.mmg.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.icons.filled.Refresh


@Composable
fun MmgScreen(
    viewModel: MmgViewModel,
    navController: NavController
) {
    val mmgList by viewModel.mmgList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMmgDtos()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mitmachgeschichten",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f),

            )
            Button(
                onClick = {
                    viewModel.emptyMmgList()
                    viewModel.loadMmgDtos()
                },
                modifier = Modifier.height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Reload",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Geschichten laden",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mmgList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(mmgList) { mmg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(80.dp)
                            .clickable {
                                navController.navigate("step")
                                viewModel.loadMmgSteps(mmg.id)
                                viewModel.resetStepCount()
                            },
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            val imageBitmap = mmg.storyIconBase64?.let { viewModel.base64ToBitmap(it) }

                            if (imageBitmap != null) {
                                Image(
                                    bitmap = imageBitmap,
                                    contentDescription = "Story Icon",
                                    modifier = Modifier.size(80.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.default_story_icon),
                                    contentDescription = "Default Story Icon",
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                            Text(text = mmg.name, style = MaterialTheme.typography.bodyLarge)

                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play")
                        }
                    }
                }
            }
        }
    }
}