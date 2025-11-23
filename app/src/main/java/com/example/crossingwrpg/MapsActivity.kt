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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.crossingwrpg.data.UserViewModel
import com.example.crossingwrpg.data.InventoryViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

enum class WalkingState { Idle, Walking, Paused }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsWithPedometerScreen(
    pedometer: Pedometer,
    stopwatch: Stopwatch,
    navController: NavHostController,
    walkingStateManager: WalkingStateManager,
    userVm: UserViewModel,
    inventoryVm: InventoryViewModel
) {
    val context = LocalContext.current
    val osTotalStepCount by pedometer.stepCount.collectAsState(initial = 0L)

    var appTotalSteps: Long by remember {
        mutableStateOf(walkingStateManager.appTotalSteps)
    }

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

    var stepsBeforePause by remember {
        mutableStateOf(walkingStateManager.stepsBeforePause)
    }

    var earnedItemsList by remember {
        mutableStateOf<Map<Long, Int>>(walkingStateManager.earnedItemsList)
    }
    var isResuming by remember {
        mutableStateOf(walkingStateManager.isResuming)
    }

    val allItems by inventoryVm.allItems.collectAsState(initial = emptyList())

    val sessionSteps = (osTotalStepCount - initialSessionSteps).coerceAtLeast(0L)

    val notifications = remember { Notifications(context).also { it.initChannel() } }

    LaunchedEffect(walkState, initialSessionSteps, isPedometerActive, appTotalSteps) {
        walkingStateManager.walkState = walkState
        walkingStateManager.initialSessionSteps = initialSessionSteps
        walkingStateManager.isPedometerActive = isPedometerActive
        walkingStateManager.appTotalSteps = appTotalSteps
        walkingStateManager.stepsBeforePause = stepsBeforePause
        walkingStateManager.earnedItemsList = earnedItemsList
        walkingStateManager.isResuming = isResuming
    }

    LaunchedEffect(Unit) {
        if (walkingStateManager.earnedItemsList.isNotEmpty() && earnedItemsList.isEmpty()) {
            earnedItemsList = walkingStateManager.earnedItemsList
        }
    }

    LaunchedEffect(sessionSteps, walkState) {
        if (walkState == WalkingState.Walking) {
            if (sessionSteps < 10) {
                return@LaunchedEffect
            }

            var currentEarnedMap = earnedItemsList.toMutableMap()
            var rewardsGranted = false

            allItems.forEach { itemTemplate ->
                val threshold = itemTemplate.dropThreshold
                val maxDropsPossible = (sessionSteps / threshold).toInt()
                val currentDropsRecorded = currentEarnedMap[itemTemplate.itemId] ?: 0

                if (maxDropsPossible > currentDropsRecorded) {
                    val newDrops = maxDropsPossible - currentDropsRecorded

                    inventoryVm.addEarnedItem(itemTemplate.itemId, newDrops)
                    currentEarnedMap[itemTemplate.itemId] = maxDropsPossible
                    rewardsGranted = true
                }
            }
            if (rewardsGranted) {
                earnedItemsList = currentEarnedMap

                val newItemMessages = mutableListOf<String>()
                earnedItemsList.forEach { (itemId, count) ->
                    val itemName = allItems.find { it.itemId == itemId }?.name ?: "Item"
                    newItemMessages.add("$count $itemName")
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

    LaunchedEffect(isPedometerActive, sessionSteps) {
        if (!isPedometerActive) return@LaunchedEffect
    }

    MusicPlayer.pause()

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
                        if (osTotalStepCount >= 0L) {
                            PixelText(
                                text = "Total Steps: $appTotalSteps",
                                fontSize = 30.sp
                            )
                        }
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
                                    earnedItemsList = emptyMap()
                                    inventoryVm.clearSessionItems()
                                    // Sets Steps to 0 every start of a new walk session
                                    initialSessionSteps = osTotalStepCount
                                    isResuming = false
                                    pedometer.start()
                                    stopwatch.start()
                                    pedometer.debugAddSteps(20)
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
                                    stepsBeforePause = sessionSteps
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

                                    userVm.recordWalk(sessionSteps.toInt(), elapsedTime)

                                    appTotalSteps += sessionSteps

                                    navController.navigate("health_stats?steps=$sessionSteps&time=$elapsedTime")
                                    walkState = WalkingState.Idle
                                    earnedItemsList = emptyMap()
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
                                    initialSessionSteps = osTotalStepCount - stepsBeforePause
                                    isResuming = true
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

                                    userVm.recordWalk(sessionSteps.toInt(), elapsedTime)

                                    appTotalSteps += sessionSteps

                                    navController.navigate("health_stats?steps=$sessionSteps&time=$elapsedTime")

                                    walkState = WalkingState.Idle
                                    earnedItemsList = emptyMap()
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