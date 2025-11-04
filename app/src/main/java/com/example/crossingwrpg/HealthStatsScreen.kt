package com.example.crossingwrpg

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HealthStatsScreen(
    steps: Int = 0,
    time: Int = 0,
    totalSteps: Long = 0
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            PixelText(
                "Walk Summary",
                fontSize = 50.sp,
            )
            Spacer(
                Modifier.height(16.dp)
            )
            PixelText(
                "Steps taken: $steps",
                fontSize = 35.sp,
            )
            PixelText(
                "Time elapsed: ${time}s",
                fontSize = 35.sp,
            )
            PixelText(
                "Total steps: $totalSteps",
                fontSize = 35.sp,
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}