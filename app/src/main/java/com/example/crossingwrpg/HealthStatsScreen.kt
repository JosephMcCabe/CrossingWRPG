package com.example.crossingwrpg

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.crossingwrpg.data.UserViewModel

@Composable
fun ItemWithCount(
    name: String,
    itemID: Int,
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PixelText(
            name,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(4.dp))

        Box(
            modifier = modifier.wrapContentSize()
        ) {
            Image(
                painter = painterResource(itemID),
                contentDescription = name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp)
            )

            if (count > 1) {
                PixelText(
                    "x$count",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp)
                        .background(Color.White, CircleShape)
                        .border(BorderStroke(1.dp, Color.Black), CircleShape)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun HealthStatsScreen(
    steps: Int = 0,
    time: Int = 0,
    navController: NavController
) {
    val userVm: UserViewModel = viewModel()
    val user = userVm.userFlow.collectAsState(initial = null).value

    val earnedItems = user?.sessionItems ?: emptyList()
    val totalItemCount = earnedItems.sumOf { it.count }

    var showRewardDialog by remember { mutableStateOf(false) }

    LaunchedEffect(earnedItems, steps) {
        if (earnedItems.isNotEmpty() && steps >= 10) {
            showRewardDialog = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        )
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
                "Total steps: ${user?.totalSteps ?: 0}",
                fontSize = 35.sp,
            )
            PixelText(
                "Total time: ${user?.totalWalkingSeconds ?: 0}",
                fontSize = 35.sp
            )
            Spacer(Modifier.height(32.dp))
            if (steps >= 10 && earnedItems.isNotEmpty()) {
                PixelText(
                    "Items found: $totalItemCount",
                    fontSize = 35.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    earnedItems.forEach { item ->
                        ItemWithCount(
                            name = item.name,
                            itemID = item.drawableId,
                            count = item.count,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                }
            }
        }
    }
    if (showRewardDialog) {
        AlertDialog(
            onDismissRequest = {
                showRewardDialog = false
            },
            containerColor = Color.White,
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PixelText(
                        "ITEMS FOUND!",
                        fontSize = 30.sp
                    )
                }
            },
            text = {
                val message = if (totalItemCount == 1) {
                    "As you were walking you picked up 1 item!"
                } else {
                    "As you were walking you came across $totalItemCount items!"
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PixelText(
                        message,
                        fontSize = 25.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRewardDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.8f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    PixelText(
                        "OK",
                        fontSize = 25.sp
                    )
                }
            }
        )
    }
}