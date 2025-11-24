package com.example.crossingwrpg

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crossingwrpg.data.UserViewModel
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt


fun manageWalkService(context: Context, action: String) {
    val i = Intent(context.applicationContext, WalkService::class.java).setAction(action)
    if (action == WalkService.ACTION_START) {
        startForegroundService(context.applicationContext,i)
    } else {
        context.startService(i)
    }
}

@Composable
fun WalkingScreen(
    navController: NavHostController,
) {
    val userVm: UserViewModel = viewModel()
    val context = LocalContext.current
    val notifications = remember {
        Notifications(context.applicationContext).apply { initChannel() }
    }
    val walkingVm = remember {
        WalkingViewModel.WalkingViewModel(notifications, context.applicationContext)
    }

    val walking = walkingVm.ui.collectAsState().value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ScrollingBackground(
            isScrolling = walking.walkState == WalkingState.Walking
        )

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
                                    manageWalkService(context, WalkService.ACTION_START)
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
                                    manageWalkService(context, WalkService.ACTION_PAUSE)
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
                                    manageWalkService(context, WalkService.ACTION_STOP)
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
                                    manageWalkService(context, WalkService.ACTION_RESUME)
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
                                    manageWalkService(context, WalkService.ACTION_STOP)
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

@Composable
private fun ScrollingBackground(
    isScrolling: Boolean,
    speedPxPerSec: Float = 60f
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val anim = remember { Animatable(0f) }

        LaunchedEffect(isScrolling, widthPx, speedPxPerSec) {
            if (!isScrolling || widthPx <= 0f || speedPxPerSec <= 0f) return@LaunchedEffect
            while (isActive) {
                val duration = ((widthPx / speedPxPerSec) * 1000f).toInt().coerceAtLeast(1)
                anim.snapTo(0f)
                anim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = duration, easing = LinearEasing)
                )
            }
        }

        val offset = - (anim.value * widthPx)
        val firstX = offset.roundToInt()
        val secondX = (offset + widthPx).roundToInt()

        Image(
            painter = painterResource(R.drawable.background_layer_1),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(firstX, 0) },
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(R.drawable.background_layer_1),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(secondX, 0) },
            contentScale = ContentScale.Crop
        )
    }
}