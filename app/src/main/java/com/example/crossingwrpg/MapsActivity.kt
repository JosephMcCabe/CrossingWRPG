package com.example.crossingwrpg


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat.startForegroundService

class MapsActivity : ComponentActivity() {

    private val battleSimulation = BattleSimulation()
    private val pedometer = Pedometer(
        context = applicationContext,
        battleSimulation = battleSimulation
    )
    private val stopwatch = Stopwatch()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pedometer.start()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                MapsWithPedometerScreen(
                    pedometer = pedometer,
                    stopwatch = stopwatch,
                    navController = navController
                )
            }
        }
    }
}

enum class WalkingState { Idle, Walking, Paused }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsWithPedometerScreen(
    pedometer: Pedometer,
    stopwatch: Stopwatch,
    navController: NavHostController
) {
    val userVm: com.example.crossingwrpg.data.UserViewModel = viewModel()
    val context = LocalContext.current
    val totalStepCount by pedometer.stepCount.collectAsState()
    val elapsedTime by stopwatch.elapsedTime.collectAsState()

    var walkState by remember {
        mutableStateOf(WalkingState.Idle)
    }

    var initialSessionSteps by remember {
        mutableStateOf(0L)
    }

    var totalStepsAtIdle by remember {
        mutableStateOf(0L)
    }

    val sessionSteps = (totalStepCount - initialSessionSteps).coerceAtLeast(0L)


    val notifications = remember { Notifications(context).also { it.initChannel() } }
    var isPedometerActive by remember { mutableStateOf(false) }
    // Reset everything each time the Walk screen is shown
    LaunchedEffect(Unit) {
        stopwatch.stop()
        stopwatch.reset()

        pedometer.stop()

        walkState = WalkingState.Idle
        isPedometerActive = false
    }

    LaunchedEffect(totalStepCount) {
        if (walkState == WalkingState.Idle) {
            totalStepsAtIdle = totalStepCount
        }
    }

    LaunchedEffect(isPedometerActive, sessionSteps) {
        if (!isPedometerActive) return@LaunchedEffect
    }

    val currentSessionSteps = rememberUpdatedState(sessionSteps)

    LaunchedEffect(isPedometerActive) {
        if (!isPedometerActive) return@LaunchedEffect
        while (isPedometerActive) {
            delay(10000)
            val stepsForNotification = currentSessionSteps.value
            if (stepsForNotification > 0) {
                notifications.postLevelUp("Walking leveled you up! Steps: $stepsForNotification")
            }
        }
    }

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
                        PixelText(
                            text = "Total Steps: $totalStepCount",
                            fontSize = 30.sp
                        )
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
                    // initial button state showing a start button
                    when(walkState){
                        WalkingState.Idle -> {
                            Button(
                                onClick = {
                                    // Sets Steps to 0 every start of a new walk session
                                    initialSessionSteps = totalStepCount
                                    pedometer.start()
                                    stopwatch.start()
                                    walkState = WalkingState.Walking
                                    isPedometerActive = true
                                    startForegroundService("Start")
                                   // val intent = Intent(context, HealthServices::class.java).apply {  }
                                    //ServiceCompat.startForeground(this, 100, foregroundServicesNotification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)

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

                                    userVm.recordWalk(sessionSteps.toInt(), elapsedTime)

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