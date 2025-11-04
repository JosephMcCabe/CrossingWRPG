package com.example.crossingwrpg

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
// Defines all navigation screens in the app.
enum class Destination(
    val route: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    BATTLE(
        route = "story_page",
        icon = Icons.Default.AutoStories,
        contentDescription = "Battle Screen"
    ),
    HOME(
        route = "home_page",
        icon = Icons.Default.Home,
        contentDescription = "Home Screen"
    ),
    WALK_MAP(
        route = "walk_screen",
        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
        contentDescription = "Walk Map Screen"
    )
}