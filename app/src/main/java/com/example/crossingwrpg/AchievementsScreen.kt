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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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


    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
            .padding(16.dp)
            .background(Color.White)
    )
    {
        Row() {
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
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
            .padding(16.dp)
            .background(Color.White)
    )
    {
        ElevatedCard() {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Trophy Image",
            )
            Text(
                text = " A New Beginning ",
                fontFamily = pixelFontFamily,
                textAlign = TextAlign.Center,
                fontSize = 35.sp,
                color = Color.DarkGray,
                modifier = Modifier
            )
            Text(
                text = " Walk 1000 steps ",
                fontFamily = pixelFontFamily,
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                color = Color.DarkGray,
                modifier = Modifier
            )
        }
    }
}
}
@Preview
@Composable
private fun AchievementItemPreview() {
    
}





