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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlin.collections.associateBy
import kotlin.collections.sumOf

const val ITEM_REWARD_THRESHOLD = 10
enum class WalkingState { Idle, Walking, Paused }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsWithPedometerScreen(
    pedometer: Pedometer,
    stopwatch: Stopwatch,
    navController: NavHostController,
    walkingStateManager: WalkingStateManager
) {
    val userVm: com.example.crossingwrpg.data.UserViewModel = viewModel()
    val context = LocalContext.current
    val totalStepCount by pedometer.stepCount.collectAsState(initial = 0L)
    val elapsedTime by stopwatch.elapsedTime.collectAsState()

    var walkState by remember {
        mutableStateOf(walkingStateManager.walkState)
    }

    var initialSessionSteps by remember {
        mutableStateOf(walkingStateManager.initialSessionSteps)
    }
    var isPedometerActive by remember {
        mutableStateOf(walkingStateManager.isPedometerActive)
    }

    var earnedItemsList by remember {
        mutableStateOf(emptyList<EarnedItem>())
    }

    val sessionSteps = (totalStepCount - initialSessionSteps).coerceAtLeast(0L)

    val notifications = remember { Notifications(context).also { it.initChannel() } }

    LaunchedEffect(walkState, initialSessionSteps, isPedometerActive) {
        walkingStateManager.walkState = walkState
        walkingStateManager.initialSessionSteps = initialSessionSteps
        walkingStateManager.isPedometerActive = isPedometerActive
    }


    LaunchedEffect(sessionSteps) {
        if (walkState == WalkingState.Walking) {
            if (sessionSteps < 10) {
                return@LaunchedEffect
            }

            var currentEarnedMap = earnedItemsList.associateBy { it.id }.toMutableMap()
            var rewardsGranted = false

            ALL_DROPPABLE_ITEMS.forEach { itemTemplate ->
                val threshold = itemTemplate.dropThreshold
                val maxDropsPossible = (sessionSteps / threshold).toInt()
                val existingItem = currentEarnedMap[itemTemplate.id]
                val currentDropsRecorded = existingItem?.count ?: 0

                if (maxDropsPossible > currentDropsRecorded) {
                    val newDrops = maxDropsPossible - currentDropsRecorded

                    if ( existingItem != null) {
                        val updatedCount = existingItem.count + newDrops
                        currentEarnedMap[itemTemplate.id] = existingItem.copy(count = updatedCount)
                    } else {
                        currentEarnedMap[itemTemplate.id] = itemTemplate.copy(count = newDrops)
                    }
                    rewardsGranted = true
                }
            }
            if (rewardsGranted) {
                val oldItemsList = earnedItemsList.associateBy { it.id }
                earnedItemsList = currentEarnedMap.values.toList()

                val newItemMessages = mutableListOf<String>()
                earnedItemsList.forEach { finalEarnedItem ->
                    val oldQuantity = oldItemsList[finalEarnedItem.id]?.count ?:0

                    val addedCount = finalEarnedItem.count - oldQuantity

                    if (addedCount > 0) {
                        newItemMessages.add("$addedCount ${finalEarnedItem.name}")
                    }
                }
                val notificationMessage = newItemMessages.joinToString(separator = ", ")

                if (notificationMessage.isNotEmpty()) {
                    notifications.showItemNotification(
                        title = "As you were walking...",
                        message = "You found: $notificationMessage"
                    )
                }
            }
        }
    }
    val totalItemsCount = earnedItemsList.sumOf { it.count }
    LaunchedEffect(isPedometerActive, sessionSteps) {
        if (!isPedometerActive) return@LaunchedEffect
    }

    val currentTotalStepCount = rememberUpdatedState(totalStepCount)

    val channelIslands = LatLng(34.161767, -119.043377)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(channelIslands, 13f)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
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
                when (walkState) {
                    WalkingState.Idle -> {
                    }
                    WalkingState.Walking, WalkingState.Paused -> {
                        PixelText(
                            text = "Steps: $sessionSteps",
                            fontSize = 30.sp
                        )
                        PixelText(
                            text = "Time: ${elapsedTime}s",
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
                    when(walkState){
                        WalkingState.Idle -> {
                            Button(
                                onClick = {
                                    stopwatch.reset()
                                    earnedItemsList = emptyList()
                                    // Sets Steps to 0 every start of a new walk session
                                    initialSessionSteps = currentTotalStepCount.value
                                    pedometer.start()
                                    stopwatch.start()
                                    walkState = WalkingState.Walking
                                    isPedometerActive = true
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
                                    pedometer.stop()
                                    stopwatch.stop()
                                    walkState = WalkingState.Paused
                                    isPedometerActive = false
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
                                    pedometer.stop()
                                    stopwatch.stop()
                                    isPedometerActive = false

                                    userVm.recordWalk(sessionSteps.toInt(), elapsedTime, earnedItemsList)
                                    earnedItemsList = emptyList()

                                    navController.navigate("health_stats?steps=$sessionSteps&time=$elapsedTime")
                                    walkState = WalkingState.Idle
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
                                    pedometer.start()
                                    stopwatch.start()
                                    walkState = WalkingState.Walking
                                    isPedometerActive = true
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
                                    pedometer.stop()
                                    stopwatch.stop()
                                    isPedometerActive = false

                                    userVm.recordWalk(sessionSteps.toInt(), elapsedTime, earnedItemsList)
                                    earnedItemsList = emptyList()
                                    initialSessionSteps = 0L
                                    navController.navigate("health_stats?steps=$sessionSteps&time=$elapsedTime")

                                    walkState = WalkingState.Idle
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