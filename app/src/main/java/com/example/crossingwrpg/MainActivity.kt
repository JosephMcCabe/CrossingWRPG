package com.example.crossingwrpg

import android.media.MediaPlayer
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.crossingwrpg.com.example.crossingwrpg.AchievementsScreenFunction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Create media player variable to be used across all screens
var mediaPlayer: MediaPlayer? = null

class MainActivity : ComponentActivity() {
    private val battleSimulation = BattleSimulation()
    private lateinit var pedometer: Pedometer
    private val stopwatch = Stopwatch()
    private val walkingStateManager = WalkingStateManager()
    private lateinit var notifications: Notifications


    override fun onPause() {
        super.onPause()
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
    }

    override fun onResume() {
        super.onResume()
        if (inBattle) {
            if (!battleWon) {
                mediaPlayer = MediaPlayer.create(this, R.raw.xdeviruchidecisivebattle)
                mediaPlayer?.start()
                mediaPlayer?.isLooping = true
            } else {
                mediaPlayer = MediaPlayer.create(this, R.raw.victory1)
                mediaPlayer?.start()
                mediaPlayer?.isLooping = true
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        pedometer = Pedometer(
            context = applicationContext,
            battleSimulation = battleSimulation
        )
        notifications = Notifications(applicationContext).apply { initChannel() }
        pedometer.start()

        lifecycleScope.launch {
            while (true) {
                delay(10000)

                if (walkingStateManager.walkState == WalkingState.Walking) {
                    val stepsForNotification = pedometer.stepCount.value - walkingStateManager.initialSessionSteps
                    if (stepsForNotification >= 0) {
                        notifications.postLevelUp("Walking leveled you up! Steps: $stepsForNotification")
                    }
                }
            }
        }

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                AppNavigation(
                    pedometer = pedometer,
                    stopwatch = stopwatch,
                    battleSimulation = battleSimulation,
                    walkingStateManager = walkingStateManager
                )
            }
        }
    }
}

@Preview
@Composable
fun AppNavigation(
    pedometer: Pedometer,
    stopwatch: Stopwatch,
    battleSimulation: BattleSimulation,
    walkingStateManager: WalkingStateManager,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    // Define initial screen when opening app
    val startDestination = Destination.HOME

    // Get currently visible screen
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val baseRoute = currentRoute?.substringBefore("?")

    val context = LocalContext.current

    if (currentRoute != "story_page") {
        mediaPlayer?.pause()
        inBattle = false
    }

    if (currentRoute == "story_page") {
        if (inBattle && !battleWon) {
                mediaPlayer = MediaPlayer.create(context, R.raw.xdeviruchidecisivebattle)
                mediaPlayer?.start()
                mediaPlayer?.isLooping = true
            }

        mediaPlayer?.start()
        inBattle = true
    }

    // Scaffold provides overall screen structure
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                // Builds a navigation item for each defined destination
                Destination.entries.forEach { destination ->
                    val isSelected = baseRoute == destination.route

                    NavigationBarItem(
                        selected = isSelected,

                        onClick = {
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
                HomePage()
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
                    stopwatch = stopwatch,
                    walkingStateManager = walkingStateManager
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
                    time = time,
                    navController = navController
                )
            }
            composable(route = Destination.ACHIEVEMENTS_SCREEN.route) {
                AchievementsScreenFunction(navController = navController)
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