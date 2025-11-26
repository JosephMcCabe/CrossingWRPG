package com.example.crossingwrpg

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.crossingwrpg.data.InventoryViewModel
import com.example.crossingwrpg.data.UserViewModel

@Composable
fun HealthStatsScreen(
    steps: Int = 0,
    time: Int = 0,
    userVm: UserViewModel,
    inventoryVm: InventoryViewModel,
    navController: NavHostController
) {
    val user = userVm.userFlow.collectAsState(initial = null).value

    val earnedItems by inventoryVm.sessionEarnedItem.collectAsState()
    val allItems by inventoryVm.allItems.collectAsState(initial = emptyList())

    val totalItemCount = earnedItems.values.sum()
    val hasEarnedRewards = earnedItems.isNotEmpty()

    var showRewardDialog by remember { mutableStateOf(false) }
    var showInventoryDialog by remember { mutableStateOf(false) }
    var displayedEarnedItems by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }

    LaunchedEffect(steps, totalItemCount) {
        if (hasEarnedRewards && displayedEarnedItems.isEmpty()) {
            displayedEarnedItems = earnedItems
            showRewardDialog = true
        }
    }
    LaunchedEffect(showRewardDialog) {
        if (!showRewardDialog && earnedItems.isNotEmpty()) {
            inventoryVm.commitEarnedItems()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            if (earnedItems.isNotEmpty()) {
                inventoryVm.commitEarnedItems()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.scroll_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PixelText(
                "Walk Summary",
                fontSize = 50.sp
            )
            Spacer(Modifier.height(16.dp))

            PixelText(
                "Steps taken: $steps",
                fontSize = 35.sp
            )
            PixelText(
                "Time elapsed: ${time}s",
                fontSize = 35.sp
            )
            PixelText(
                "Total steps: ${user?.totalSteps ?: 0}",
                fontSize = 35.sp
            )
            PixelText(
                "Total time: ${user?.totalWalkingSeconds ?: 0}s",
                fontSize = 35.sp
            )
            Spacer(Modifier.height(20.dp))
            PixelText(
                "Walk Boost",
                fontSize = 40.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                PixelText(
                    "Attack Speed: ${user?.speed}",
                    fontSize = 35.sp
                )
                Spacer(Modifier.width(8.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.greenarrow)
                        .decoderFactory(GifDecoder.Factory())
                        .build(),
                    contentDescription = "Speed increase",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(32.dp))
            if (displayedEarnedItems.isNotEmpty()) {
                EarnedItemsDisplay(
                    earnedItems = displayedEarnedItems,
                    allItems = allItems
                )
            }
            Spacer(Modifier.height(50.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = "Inventory",
                    onClick = {
                        if (earnedItems.isNotEmpty()) {
                            inventoryVm.commitEarnedItems()
                        }
                        showInventoryDialog = true
                    }
                )
                ActionButton(
                    text = "Continue",
                    onClick = {
                        navController.navigate("narrative_page")
                    }
                )
            }
        }
    }
    if (showRewardDialog) {
        RewardDialog(
            totalItemCount = totalItemCount,
            onDismiss = { showRewardDialog = false }
        )
    }
    if (showInventoryDialog) {
        InventoryScreen(
            inventoryVm = inventoryVm,
            onDismiss = { showInventoryDialog = false }
        )
    }
}