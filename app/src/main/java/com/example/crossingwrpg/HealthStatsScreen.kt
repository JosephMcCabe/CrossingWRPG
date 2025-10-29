package com.example.crossingwrpg

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HealthStatsScreen(steps: Int = 0, time: Int = 0) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text(
                "Walk Summary",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(
                Modifier.height(16.dp)
            )
            Text(
                "Steps taken: $steps",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Time elapsed: ${time}s",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}