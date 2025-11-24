package com.example.crossingwrpg.com.example.crossingwrpg

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crossingwrpg.MusicPlayer
import com.example.crossingwrpg.pixelFontFamily


private var achievementsCompleted = 0
@Composable
fun AchievementsScreenFunction(navController: NavHostController) {

    val userVm: com.example.crossingwrpg.data.UserViewModel = viewModel()
    val user = userVm.userFlow.collectAsState(initial = null).value

    val userSteps = user?.totalSteps ?: 0
    val totalEnemiesDefeat = user?.enemiesDefeated ?: 0
    val userTime = user?.totalWalkingSeconds ?: 0

    achievementsCompleted = 0


    MusicPlayer.pause()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    )
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 35.dp)
            .padding(16.dp)
            .background(Color.White)
    )
    {
        Row {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Trophy Image",
            )
            Text(
                text = " Achievements",
                fontFamily = pixelFontFamily,
                textAlign = TextAlign.Center,
                fontSize = 35.sp,
                color = Color.DarkGray,
                modifier = Modifier
            )
        }
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(Color.White)
        )
        {
            item {
                AchievementCard(
                    achievementName = "A New Beginning",
                    achievementDescription = "Walk 1,000 steps",
                    achievementProgress = userSteps / 1000.0
                )}
            item{
                AchievementCard(
                    achievementName = "Making Progress",
                    achievementDescription = "Walk 10,000 Steps",
                    achievementProgress = userSteps / 10000.0
                )}
            item{
                AchievementCard(
                    achievementName = "True Walker",
                    achievementDescription = "Walk 100,000 Steps",
                    achievementProgress = userSteps / 100000.0
                )}
            item{
                AchievementCard(
                    achievementName = "New Slayer",
                    achievementDescription = "Defeat 5 Enemies",
                    achievementProgress = totalEnemiesDefeat / 5.0
                )}
            item{
                AchievementCard(
                    achievementName = "Dungeon Crawler",
                    achievementDescription = "Defeat 25 enemies",
                    achievementProgress = totalEnemiesDefeat / 25.0
                )}
            item{
                AchievementCard(
                    achievementName = "Be Right Back",
                    achievementDescription = "Walk for a total of 30 minutes",
                    achievementProgress = userTime / (30.0 * 60.0)
                )}
            item{
                AchievementCard(
                    achievementName = "???",
                    achievementDescription = "Unlock more achievements to unlock",
                    achievementProgress = 0.0 / 1.0
                )}
        }
    }
}


@Composable
fun AchievementCard(
    achievementName: String,
    achievementDescription: String,
    achievementProgress: Double,
) {
    // Set Achievement progress bar to accurately show progress to
    var achievementPercentCompleted = achievementProgress
    achievementPercentCompleted = (achievementPercentCompleted * 100) * 2.75

    var localName = achievementName
    var localDescription = achievementDescription

    if (achievementDescription == "Unlock more achievements to unlock" && achievementsCompleted >= 6) {
        localName = "Walking Hero"
        localDescription = "Unlock all achievements in Crossing: A Walking RPG"
        achievementPercentCompleted= 275.0
    }

    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 15.dp)
            .width(300.dp)
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Trophy Image",
        )
            Text(
                text = " $localName ",
                fontFamily = pixelFontFamily,
                fontSize = 35.sp,
                color = Color.DarkGray,
                modifier = Modifier
            )
        Text(
            text = " $localDescription ",
            fontFamily = pixelFontFamily,
            fontSize = 25.sp,
            color = Color.DarkGray,
            modifier = Modifier
        )
        if (achievementPercentCompleted < 275.0) {
            LinearProgressIndicator(
                progress = { 1F },
                modifier = Modifier
                    .padding(vertical = 1.dp)
                    .height(10.dp)
                    .width(achievementPercentCompleted.dp)

            )
        }
        else {
            achievementsCompleted++
            Text(
                text = " Completed! ",
                fontFamily = pixelFontFamily,
                fontSize = 30.sp,
                color = Color.DarkGray,
                modifier = Modifier
            )

        }
    }
}