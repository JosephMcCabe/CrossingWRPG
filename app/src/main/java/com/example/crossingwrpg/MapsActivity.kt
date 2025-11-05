package com.example.crossingwrpg


import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MapsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                MapsWithPedometerScreen(navController = navController)
            }
        }
    }
}

enum class WalkingState { Idle, Walking, Paused}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsWithPedometerScreen(
    pedometer: Pedometer? = null,
    stopwatch: Stopwatch? = null,
    navController: NavHostController
) {
    val context = LocalContext.current
    val pedometer = remember { pedometer ?: Pedometer(context) }
    val stopwatch = remember { stopwatch ?: Stopwatch() }

    val stepCount by pedometer.stepCount.collectAsState()
    val elapsedTime by stopwatch.elapsedTime.collectAsState()

    var walkState by remember { mutableStateOf(WalkingState.Idle) }


    val notifications = remember { Notifications(context).also {it.initChannel()} }
    var isPedometerActive by remember { mutableStateOf(false) }
    // Reset everything each time the Walk screen is shown
    LaunchedEffect(Unit) {
        stopwatch.stop()
        stopwatch.reset()

        pedometer.stop()
        pedometer.reset()

        walkState = WalkingState.Idle
        isPedometerActive = false
    }

    LaunchedEffect(isPedometerActive) {
        if (!isPedometerActive) return@LaunchedEffect
        while (isPedometerActive) {
            kotlinx.coroutines.delay(5000)
            notifications.postLevelUp("Walking leveled you up! Steps: $stepCount")
        }
    }

    val channelIslands = LatLng(34.161767, -119.043377)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(channelIslands, 13f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Steps: $stepCount", style = MaterialTheme.typography.titleLarge)
                Text(text = "Time: ${elapsedTime}s", style = MaterialTheme.typography.titleLarge)

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    // initial button state showing a start button
                    when(walkState){
                        WalkingState.Idle -> {
                            Button(onClick = {
                                pedometer.start()
                                stopwatch.start()
                                walkState = WalkingState.Walking
                                isPedometerActive = true
                            }) {
                                Text(
                                    text = "Start",
                                    fontFamily = pixelFontFamily
                                )
                            }
                        }
                        WalkingState.Walking -> {
                            Button(onClick = {
                                pedometer.stop()
                                stopwatch.stop()
                                walkState = WalkingState.Paused
                                isPedometerActive = false
                            }) {
                                Text(
                                    text = "Pause",
                                    fontFamily = pixelFontFamily
                                )
                            }
                            Button(onClick = {
                                pedometer.stop()
                                stopwatch.stop()
                                isPedometerActive = false

                                navController.navigate("health_stats?steps=$stepCount&time=$elapsedTime")
                                walkState = WalkingState.Idle
                            }) {
                                Text(
                                    text = "Stop",
                                    fontFamily = pixelFontFamily
                                )
                            }
                        }
                        WalkingState.Paused -> {
                            Button(onClick = {
                                pedometer.start()
                                stopwatch.start()
                                walkState = WalkingState.Walking
                                isPedometerActive = true
                            }) {
                                Text(
                                    text = "Continue",
                                    fontFamily = pixelFontFamily
                                )
                            }
                            Button(onClick = {
                                pedometer.stop()
                                stopwatch.stop()
                                isPedometerActive = false

                                navController.navigate("health_stats?steps=$stepCount&time=$elapsedTime")
                                walkState = WalkingState.Idle
                            }){
                                Text(
                                    text = "Stop",
                                    fontFamily = pixelFontFamily
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

