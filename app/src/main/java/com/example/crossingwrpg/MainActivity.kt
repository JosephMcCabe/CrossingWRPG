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

@Preview
@Composable
fun AppNavigation() {
    var currentScreen by remember {mutableStateOf("home_page")}

    when (currentScreen) {
        "map_screen" -> WalkMapScreen(
            onNavigateToWalk = { currentScreen = "home_page"}
        )
        "home_page" -> HomePage(
            onNavigateToHome = { currentScreen = "map_screen"},
        )
    }
}
