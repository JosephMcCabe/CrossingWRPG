package com.example.crossingwrpg


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.LocationServices


import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task


//Prepares app for location API
private lateinit var fusedLocationClient: FusedLocationProviderClient

private lateinit var locationCallback: LocationCallback

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_maps)



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fun requestLocationPermission() {
            when {
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    -> { //Permission accepted

                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    //Additional reasoning shown
                }
                else -> { //Permission not asked, ask permission
                    requestAndroidPermission.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            }

        }
        requestLocationPermission()

    }



    private val requestAndroidPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {isGranted: Boolean ->
            if (isGranted) {

            } else {

            }
        }



    public var varLatitude = 0.0
    public var varLongitude = 0.0

    fun createLocationRequest() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
    }




    override fun onResume() {
        super.onResume()
        //if (requestingLocationUpdates) startLocationUpdates()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates() {
       // fusedLocationClient.requestLocationUpdates(locationRequest,
       //     locationCallback,
       //     Looper.getMainLooper())
    }

    //Implement non successful location settings




    override fun onMapReady(googleMap: GoogleMap) { //Map loaded
        mMap = googleMap

        fun checkLocationPermissions() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val toast = Toast.makeText(
                    this,
                    "Please accept location permissions to track your location.",
                    Toast.LENGTH_LONG
                )
                toast.show()
                return
            }
            
        }

        checkLocationPermissions()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        //Initialize Last User Location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    varLatitude = location.latitude
                    varLongitude = location.longitude
                    val currentLocation = LatLng(varLatitude, varLongitude)
                    mMap.addMarker(MarkerOptions().position(currentLocation).title("My Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

                }
                else {
                    val toast = Toast.makeText(this, "Please relocate or enable location services to track your location.", Toast.LENGTH_LONG)
                    toast.show()
                }
            }

        //Adds in marker to University Channel Islands
        val channelIslands = LatLng(34.161767, -119.043377)
        mMap.addMarker(MarkerOptions().position(channelIslands).title("California State University Channel Islands"))


    }
}