package com.example.crossingwrpg

import android.content.Intent
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    //define permission launcher
    private val requestNotifPerm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        NotifPrefs.markAsked(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (
            NotifPrefs.needsRuntimePermission() &&
            !NotifPrefs.asked(this) &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestNotifPerm.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                AppNavigation()
            }
        }
    }
}

// Sets up the app's navigation and navigation bar structure
@Preview
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    // Define initial screen when opening app
    val startDestination = Destination.HOME

    // Get currently visible screen
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Scaffold provides overall screen structure
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Build bottom navigation bar
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                // Builds a navigation item for each defined destination
                Destination.entries.forEach { destination ->
                    val isExternalWalk = destination == Destination.WALK_MAP
                    val isSelected = currentRoute == destination.route

                    NavigationBarItem(
                        selected = isSelected,

                        onClick = {
                            if (isExternalWalk) {
                                // Launches external MapsActivity
                                val createIntent = Intent(context, MapsActivity::class.java)
                                context.startActivity(createIntent)
                            } else {
                                // Standard internal navigation for HOME and BATTLE
                                navController.navigate(route = destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = {
                            Text(
                                destination.label,
                                fontFamily = pixelFontFamily,
                                fontSize = 23.sp
                            )
                        }
                    )
                }
            }
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination.route,
            modifier = Modifier.padding(contentPadding)
        ) {
            // HOME Destination
            composable(route = Destination.HOME.route) {
                HomePage(
                    onNavigateToStory = { navController.navigate(Destination.BATTLE.route) }
                )
            }
            // BATTLE Destination
            composable(route = Destination.BATTLE.route) {
                BattleScreen(
                    onNavigateToHome = { navController.navigate(Destination.HOME.route) }
                )
            }
        }
    }
}