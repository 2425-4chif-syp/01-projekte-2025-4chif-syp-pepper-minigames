package com.example.mmg.presentation

import androidx.compose.foundation.Image
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mitmachgeschichten", style = MaterialTheme.typography.headlineMedium)

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
                            .height(80.dp),
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

                            IconButton(onClick = {
                                navController.navigate("step")
                                viewModel.loadMmgSteps(mmg.id)
                                viewModel.resetStepCount()
                            }) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play")
                            }
                        }
                    }
                }
            }
        }
    }
}