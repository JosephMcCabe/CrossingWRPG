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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val battleSimulation = BattleSimulation()
    private val pedometer by lazy { Pedometer(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                pedometer.stepCount.collectLatest {
                    newSteps -> battleSimulation.updateSteps(newTotalSteps = newSteps)
                }
            }
        }

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                AppNavigation(battleSimulation = battleSimulation)
            }
        }
    }
}


// Sets up the app's navigation and navigation bar structure
@Preview
@Composable
fun AppNavigation(modifier: Modifier = Modifier, battleSimulation: BattleSimulation) {
    val navController = rememberNavController()
    val startDestination = Destination.HOME

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEach { destination ->
                    val isSelected = currentRoute == destination.route

                    NavigationBarItem(
                        selected = isSelected,

                        onClick = {
                            navController.navigate(route = destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
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
    ) {
        contentPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination.route,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(route = Destination.HOME.route) {
                HomePage(
                    battleSimulation = battleSimulation,
                    onNavigateToStory = { navController.navigate(Destination.BATTLE.route) }
                )
            }
            composable(route = Destination.BATTLE.route) {
                BattleScreen(
                    battleSimulation = battleSimulation,
                    onNavigateToHome = { navController.navigate(Destination.HOME.route) }
                )
            }
            composable(route = Destination.WALK_MAP.route) {
                MapsWithPedometerScreen()
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