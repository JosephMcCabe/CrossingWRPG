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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.crossingwrpg.com.example.crossingwrpg.AchievementsScreenFunction
import com.example.crossingwrpg.data.InventoryViewModel
import com.example.crossingwrpg.data.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private var screenName: String? = ""

class MainActivity : ComponentActivity() {
    private val battleSimulation = BattleSimulation()
    private lateinit var pedometer: Pedometer
    private val stopwatch = Stopwatch()
    private val walkingStateManager = WalkingStateManager()
    private lateinit var notifications: Notifications

    override fun onPause() {
        super.onPause()

        MusicPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        if (screenName == "story_page")
            MusicPlayer.play()
    }

    override fun onDestroy() {
        super.onDestroy()
            MusicPlayer.free()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MusicPlayer.preparePlayer(applicationContext)

        pedometer = Pedometer(
            context = applicationContext
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

@Composable
fun AppNavigation(
    pedometer: Pedometer,
    stopwatch: Stopwatch,
    battleSimulation: BattleSimulation,
    walkingStateManager: WalkingStateManager,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val userVm: UserViewModel = viewModel()
    val inventoryVm: InventoryViewModel = viewModel()

    // Define initial screen when opening app
    val startDestination = Destination.HOME

    // Get currently visible screen
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val baseRoute = currentRoute?.substringBefore("?")
    screenName = currentRoute


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
                HomePage(userVm)
            }
            composable(route = Destination.BATTLE.route) {
                battleSimulation.resetBattle()
                BattleScreen(
                    onNavigateToHome = { navController.navigate(Destination.HOME.route) },
                    battleSimulation = battleSimulation,
                    userVm = userVm,
                    inventoryVm = inventoryVm
                )
            }
            composable(route = Destination.WALK_MAP.route) {
                MapsWithPedometerScreen(
                    navController = navController,
                    pedometer = pedometer,
                    stopwatch = stopwatch,
                    walkingStateManager = walkingStateManager,
                    userVm = userVm,
                    inventoryVm = inventoryVm
                )
            }
            composable(
                route = "health_stats?steps={steps}&time={time}",
              
                arguments = listOf(
                    navArgument("steps") { type = NavType.IntType; defaultValue = 0 },
                    navArgument("time")  { type = NavType.IntType;  defaultValue = 0 }
                )
            ) { backStackEntry ->
                val steps = backStackEntry.arguments?.getInt("steps") ?: 0
                val time  = backStackEntry.arguments?.getInt("time") ?: 0
                HealthStatsScreen(
                    steps = steps,
                    time = time,
                    userVm = userVm,
                    inventoryVm = inventoryVm,
                    navController = navController
                )
            }
            composable(route = "narrative_page") {
                NarrativeScreen(navController = navController)
            }
            composable(route = Destination.ACHIEVEMENTS_SCREEN.route) {
                AchievementsScreenFunction(navController = navController)
            }
            composable(route = Destination.CHARACTER.route) {
                CharacterScreen(navController = navController)
            }
        }
    }
}