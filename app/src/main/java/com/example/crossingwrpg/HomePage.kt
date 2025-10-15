package com.example.crossingwrpg

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// A main menu screen displaying the character, game title, and navigation buttons for "Walk" and "Story."
@Composable
fun HomePage(onNavigateToWalk: () -> Unit, onNavigateToStory: () -> Unit) {
    // Box used to stack main character image, title, and buttons
    Box(
        // Box takes up entire screen
        modifier = Modifier.fillMaxSize()
    ) {
        // Column to center and contain large images
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main character image displayed in the center of screen
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 60.dp),
                painter = painterResource(R.drawable.samplecharacter),
                contentDescription = null,
                contentScale = ContentScale.FillHeight

            )
        }

        // Game Title Text
        Text(
            text = "Crossing",
            fontFamily = pixelFontFamily,
            textAlign = TextAlign.Center,
            fontSize = 70.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp)
                .padding(16.dp)
        )
        // Game Subtitle Text
        Text(
            text = "A Walking RPG",
            fontFamily = pixelFontFamily,
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            color = Color.DarkGray,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 76.dp)
                .padding(16.dp)
        )

        // Story button (Bottom-Left)
        // When button is pressed navigates to StoryPage
        Button(
            onClick = onNavigateToStory,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 70.dp)
                .size(93.dp)
        ) {
            // "Story" text on button
            Text(
                text = "Story",
                fontSize = 22.sp,
                fontFamily = pixelFontFamily,
                textAlign = TextAlign.Center
            )
        }

        // Walk Button (Bottom-Center)
        // When button is pressed navigates to WalkMapScreen
        Button(
            onClick = onNavigateToWalk,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black.copy(alpha = 0.9f)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .size(120.dp)
        ) {
            // "Walk" text on button
            Text(
                text = "Walk",
                fontSize = 30.sp,
                fontFamily = pixelFontFamily,
                textAlign = TextAlign.Center
            )
        }
    }
}