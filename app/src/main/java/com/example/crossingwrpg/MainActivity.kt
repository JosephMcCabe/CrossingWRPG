package com.example.crossingwrpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
    val battleSimulation = remember {
        BattleSimulation()
    }
    val pedometer = remember {
        Pedometer(
            context = context.applicationContext,
            battleSimulation =  battleSimulation
        )
    }
    val stopwatch = remember {
        Stopwatch()
    }

    // Define initial screen when opening app
    val startDestination = Destination.HOME

    // Get currently visible screen
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val baseRoute = currentRoute?.substringBefore("?")

    DisposableEffect(pedometer) {
        pedometer.start()
        onDispose {
            pedometer.stop()
        }
    }

    // Scaffold provides overall screen structure
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Build bottom navigation bar
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                // Builds a navigation item for each defined destination
                Destination.entries.forEach { destination ->
                    val isSelected = baseRoute == destination.route

                    NavigationBarItem(
                        selected = isSelected,

                        onClick = {
                            // Standard internal navigation
                                navController.navigate(route = destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = false
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }

                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
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
            composable(route = Destination.HOME.route) {
                HomePage(
                    onNavigateToStory = { navController.navigate(Destination.BATTLE.route) },
                )
            }
            composable(route = Destination.BATTLE.route) {
                BattleScreen(
                    onNavigateToHome = { navController.navigate(Destination.HOME.route) },
                    battleSimulation = battleSimulation
                )
            }
            composable(route = Destination.WALK_MAP.route) {
                MapsWithPedometerScreen(
                    navController = navController,
                    pedometer = pedometer,
                    stopwatch = stopwatch
                )
            }
            composable(
                route = "health_stats?steps={steps}&time={time}&totalSteps={totalSteps}",
              
                arguments = listOf(
                    navArgument("steps") { type = NavType.IntType; defaultValue = 0 },
                    navArgument("time")  { type = NavType.IntType;  defaultValue = 0 },
                    navArgument("totalSteps") { type = NavType.LongType; defaultValue = 0L }
                )
            ) { backStackEntry ->
                val steps = backStackEntry.arguments?.getInt("steps") ?: 0
                val time  = backStackEntry.arguments?.getInt("time") ?: 0
                HealthStatsScreen(
                    steps = steps,
                    time = time)
            }
        }
    }
}
@Composable
fun PixelText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 25.sp
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        fontFamily = pixelFontFamily
    )
}