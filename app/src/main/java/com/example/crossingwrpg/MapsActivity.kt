package com.example.crossingwrpg


import android.Manifest
import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MapsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MapsWithPedometerScreen()
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
) {
    val context = LocalContext.current
    val pedometer = remember { pedometer ?: Pedometer(context) }
    val stopwatch = remember { stopwatch ?: Stopwatch() }

    val stepCount by pedometer.stepCount.collectAsState()
    val elapsedTime by stopwatch.elapsedTime.collectAsState()

    var walkState by remember { mutableStateOf(WalkingState.Idle) }

    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val recognitionGranted = perms[Manifest.permission.ACTIVITY_RECOGNITION] ?: false
        if (recognitionGranted) {
            pedometer.start()
        }
    }

    LaunchedEffect(Unit) {
        requestPermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        )
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
                            }) {
                                Text(
                                    text = "Pause",
                                    fontFamily = pixelFontFamily
                                )
                            }
                            Button(onClick = {
                                pedometer.stop()
                                stopwatch.stop()
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
                            }) {
                                Text(
                                    text = "Continue",
                                    fontFamily = pixelFontFamily
                                )
                            }
                            Button(onClick = {
                                pedometer.stop()
                                stopwatch.stop()
                            }) {
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