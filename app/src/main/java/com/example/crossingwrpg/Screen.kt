package com.example.crossingwrpg

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
// Defines all navigation screens in the app.
enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    // Story/Battle Screen
    BATTLE(
        route = "story_page",
        label = "Story",
        icon = Icons.Default.AutoStories,
        contentDescription = "Battle Screen"
    ),
    // Main app home screen
    HOME(
        route = "home_page",
        label = "Home",
        icon = Icons.Default.Home,
        contentDescription = "Home Screen"
    ),
    // Map or walk screen
    WALK_MAP(
        route = "map_screen",
        label = "Walk",
        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
        contentDescription = "Walk Map Screen"
    );
}