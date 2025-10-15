package com.example.crossingwrpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

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

@Preview(showBackground = true)
@Composable
fun AppNavigation() {
    // State variable that controls which screen is visible when opening app
    var currentScreen by remember {mutableStateOf("home_page")}

    // Main composable function that switches between screens
    when (currentScreen) {
        // If current state is "map_screen"
        "map_screen" -> WalkMapScreen(
            // When "Home" button is pressed (onNavigateToHome is triggered)
            // Updates state to "home_page"
            onNavigateToHome = { currentScreen = "home_page"}
        )
        // If current state is "home_page"
        "home_page" -> HomePage(
            // When "Walk" button is pressed (onNavigateToWalk is triggered)
            // Updates state to "map_screen"
            onNavigateToWalk = { currentScreen = "map_screen"},
            onNavigateToStory = { currentScreen = "story_page"}
        )

        // If current state is "CombatScreen"
        "story_page" -> BattleScreen(
            // When "Home" button is pressed (onNavigateToHome is triggered)
            // Updates state to "home_page"
            onNavigateToHome = { currentScreen = "home_page"}
        )
    }
}
