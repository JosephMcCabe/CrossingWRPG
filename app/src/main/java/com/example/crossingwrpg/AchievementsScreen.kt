package com.example.crossingwrpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment


class AchievementsScreen : ComponentActivity() {

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
fun AchievementsScreenFunction() {

    var currentAchievementProgress by remember { mutableFloatStateOf(255f) }

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
                achievementDescription = "Walk 1000 steps",
                achievementProgress = 600.0 / 1000.0
            )}
            item{
            AchievementCard(
                achievementName = "Goblin Slaying",
                achievementDescription = "Defeat 5 Evil Goblins",
                achievementProgress = 5.0 / 5.0
            )}
            item{
                AchievementCard(
                achievementName = "A Step Up",
                achievementDescription = "Walk 100,000 Steps",
                achievementProgress = 600.0 / 100000.0
            )}
            item{
                AchievementCard(
                achievementName = "???",
                achievementDescription = "Progress in the story to unlock",
                achievementProgress = 15.0 / 100.0
            )}
        }
        Box(modifier = Modifier) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxSize()
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 15.dp)
                ) {
                    Text(
                        text = "Back",
                        fontFamily = pixelFontFamily,
                        fontSize = 35.sp,
                        color = Color.DarkGray,
                        modifier = Modifier
                            .padding(start = 15.dp, end = 15.dp)
                            .padding(top = 10.dp, bottom = 10.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun AchievementCard(
    achievementName: String,
    achievementDescription: String,
    achievementProgress: Double,
) {
    var achievementPercentCompleted = achievementProgress
    achievementPercentCompleted = (achievementPercentCompleted * 100)
    achievementPercentCompleted = (achievementPercentCompleted * 2.75)
    ElevatedCard(modifier = Modifier
        .padding(bottom = 15.dp)
        .width(275.dp)
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Trophy Image",
        )
        Text(
            text = " $achievementName ",
            fontFamily = pixelFontFamily,
            fontSize = 35.sp,
            color = Color.DarkGray,
            modifier = Modifier
        )
        Text(
            text = " $achievementDescription ",
            fontFamily = pixelFontFamily,
            fontSize = 25.sp,
            color = Color.DarkGray,
            modifier = Modifier
        )
        LinearProgressIndicator(
            progress = { 1F },
            modifier = Modifier
                .padding(vertical = 1.dp)
                .height(10.dp)
                .width(achievementPercentCompleted.dp)

        )
    }
}
