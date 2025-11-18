package com.example.crossingwrpg

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
@Composable
fun WalkingScreen(
    pedometer: Pedometer,
    stopwatch: Stopwatch,
    navController: NavHostController,
) {
    val userVm: com.example.crossingwrpg.data.UserViewModel = viewModel()
    val context = LocalContext.current
    val notifications = remember {
        Notifications(context.applicationContext).apply { initChannel() }
    }
    val walkingVm = remember {
        WalkingViewModel.WalkingViewModel(pedometer, stopwatch, notifications)
    }

    val walking = walkingVm.ui.collectAsState().value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (walking.walkState) {
                    WalkingState.Idle -> {
                        PixelText(
                            text = "Total Steps: ${walking.sessionSteps}",
                            fontSize = 30.sp
                        )
                    }
                    WalkingState.Walking -> {
                        PixelText(
                            text = "Steps: ${walking.sessionSteps}",
                            fontSize = 30.sp
                        )
                        PixelText(
                            text = "Time: ${walking.elapsedSeconds}s",
                            fontSize = 30.sp
                        )
                    }
                    WalkingState.Paused -> {
                        PixelText(
                            text = "Steps: ${walking.sessionSteps}",
                            fontSize = 30.sp
                        )
                        PixelText(
                            text = "Time: ${walking.elapsedSeconds}s",
                            fontSize = 30.sp
                        )
                    }
                }
                Spacer(
                    Modifier.height(8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when(walking.walkState){
                        WalkingState.Idle -> {
                            Button(
                                onClick = {
                                    walkingVm.onStartClicked()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Green,
                                    contentColor = Color.Black
                                )
                            ) {
                                PixelText(
                                    text = "Start",
                                    fontSize = 25.sp
                                )
                            }
                        }
                        WalkingState.Walking -> {
                            Button(
                                onClick = {
                                    walkingVm.onPauseClicked()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                )
                            ) {
                                PixelText(
                                    text = "Pause",
                                    fontSize = 25.sp
                                )
                            }
                            Button(
                                onClick = {
                                    val results = walkingVm.onStopClicked()
                                    userVm.recordWalk(results.steps, results.time, results.earnedItems)
                                    navController.navigate("health_stats?steps=${results.steps}&time=${results.time}")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.Black
                                )
                            ) {
                                PixelText(
                                    text = "Stop",
                                    fontSize = 25.sp
                                )
                            }
                        }
                        WalkingState.Paused -> {
                            Button(
                                onClick = {
                                    walkingVm.onResumeClicked()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Green,
                                    contentColor = Color.Black
                                )
                            ) {
                                PixelText(
                                    text = "Continue",
                                    fontSize = 25.sp
                                )
                            }
                            Button(
                                onClick = {
                                    val results = walkingVm.onStopClicked()
                                    userVm.recordWalk(results.steps, results.time, results.earnedItems)
                                    navController.navigate("health_stats?steps=${results.steps}&time=${results.time}")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.Black
                                )
                            ) {
                                PixelText(
                                    text = "Stop",
                                    fontSize = 25.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}