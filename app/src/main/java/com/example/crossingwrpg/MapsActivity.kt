package com.example.crossingwrpg

import android.content.Intent
import android.os.Build
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.crossingwrpg.data.UserViewModel
import com.example.crossingwrpg.data.InventoryViewModel
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

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
    val appContext = context.applicationContext

    fun sendWalkServiceAction(action: String) {
        val intent = Intent(appContext, WalkService::class.java).apply {
            this.action = action
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent)
        } else {
            appContext.startService(intent)
        }
    }
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
        if (walkState == WalkingState.Idle) {
            pedometer.start()
        }
    }

    LaunchedEffect(sessionSteps, walkState) {
        if (walkState == WalkingState.Walking) {
            if (sessionSteps < 10) {
                return@LaunchedEffect
            }

            val currentEarnedMap = earnedItemsList.toMutableMap()
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        ScrollingBackground(
            isScrolling = walkState == WalkingState.Walking,
            image = R.drawable.background_layer_1,
            speedPxPerSec = 20f
        )
        ScrollingBackground(
            isScrolling = walkState == WalkingState.Walking,
            image = R.drawable.background_layer_2,
            speedPxPerSec = 40f
        )
        ScrollingBackground(
            isScrolling = walkState == WalkingState.Walking,
            image = R.drawable.background_layer_3,
            speedPxPerSec = 60f
        )

        val showSoldier = walkState == WalkingState.Walking
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(if (showSoldier) {
                    R.drawable.soldierwalking
                } else {
                    R.drawable.pixelsoldier
                })
                .decoderFactory(ImageDecoderDecoder.Factory())
                .build(),
            contentDescription = "soldier Image",
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.Center)
                .offset(y = -20.dp),
            contentScale = ContentScale.Fit,
            filterQuality = FilterQuality.None
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
                                    pedometer.debugAddSteps(100)
                                    walkState = WalkingState.Walking
                                    isPedometerActive = true
                                    sendWalkServiceAction(WalkService.ACTION_START)
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
                                    sendWalkServiceAction(WalkService.ACTION_PAUSE)
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
                                    sendWalkServiceAction(WalkService.ACTION_STOP)

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
                                    sendWalkServiceAction(WalkService.ACTION_RESUME)
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
                                    sendWalkServiceAction(WalkService.ACTION_STOP)

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

@Composable
private fun ScrollingBackground(
    isScrolling: Boolean,
    image: Int,
    speedPxPerSec: Float,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthDp= maxHeight * (16f / 9f)
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

        val offset = -(anim.value * widthPx)
        val firstX = offset.roundToInt()
        val secondX = (offset + widthPx).roundToInt()

        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier
                .height(maxHeight / 2)
                .width(widthDp)
                .offset { IntOffset(firstX, 0) },
            contentScale = ContentScale.FillBounds
        )
        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier
                .height(maxHeight / 2)
                .width(widthDp)
                .offset { IntOffset(secondX, 0) },
            contentScale = ContentScale.FillBounds
        )
    }
}