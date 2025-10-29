package com.example.crossingwrpg


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.ComponentActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


private lateinit var fusedLocationClient: FusedLocationProviderClient

lateinit var map: GoogleMap

//Value for current position, sets default location if location not found
var currentLatLng = LatLng(34.161767, -119.043377)
var askToEnablePermissions: Boolean = true

var varLatitude = 0.0
var varLongitude = 0.0


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




@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsWithPedometerScreen( pedometer: Pedometer? = null) {
    val context = LocalContext.current
    val pedometer = remember { pedometer ?: Pedometer(context) }

    val stepCount by pedometer.stepCount.collectAsState()

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


        fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(
                context,
                "Please accept all location permissions to track your location.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

    }

    checkLocationPermissions()

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location : Location? ->
            if (location != null) {
                varLatitude = location.latitude
                varLongitude = location.longitude
                currentLatLng = LatLng(varLatitude, varLongitude)
            }
            else if (askToEnablePermissions) {
                Toast.makeText( context,
                    "Please relocate or enable location services to track your location.",
                    Toast.LENGTH_LONG)
                .show()
                askToEnablePermissions = false
            }
        }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLatLng, 13f)



    }



    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
        )
        {
            Marker(
                state = MarkerState(position = LatLng(varLatitude, varLongitude)), // Example: Sydney
                title = "Current Location",
            )

        }

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
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { pedometer.start() }) {
                        Text(
                            text = "Start",
                            fontFamily = pixelFontFamily
                        )
                    }
                    Button(onClick = { pedometer.stop() }) {
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