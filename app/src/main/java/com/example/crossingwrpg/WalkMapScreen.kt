package com.example.crossingwrpg

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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


// This screen shows a map or path and provides navigation and core game action buttons.
@Composable
fun WalkMapScreen(onNavigateToHome: () -> Unit) {
    // Box used to stack background image, title, and buttons
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Displays background image from drawable and fills the screen
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.pathimage),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        // Displays the Game Title Text
        Text(
            text = "Crossing",
            fontFamily = pixelFontFamily,
            textAlign = TextAlign.Center,
            fontSize = 70.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp)
                .background(
                    // Border is transparent by 0.7f
                    Color.Black.copy(alpha = 0.7f),
                    // Rounds the corner of the border
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        )
        // Displays Subtitle Text
        Text(
            text = "A Walking RPG",
            fontFamily = pixelFontFamily,
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            color = Color.LightGray,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 76.dp)
                .padding(16.dp)
        )

        // When button is pressed navigates to HomePage
        Button(
            onClick = onNavigateToHome,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 70.dp)
                .size(93.dp)
        ) {
            // "Home" Text on Button
            Text(
                text = "Home",
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                fontFamily = pixelFontFamily
            )
        }

        // Primary action button ("Start Walking")
        // When button is pressed navigates to TODO: add WalkScreenName here.
        Button(
            onClick = { /* Walk */ },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black.copy(alpha = 0.8f)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .size(120.dp)
        ) {
            // "Start Walking" text on button
            Text(
                text = "Start Walking",
                fontSize = 27.sp,
                textAlign = TextAlign.Center,
                fontFamily = pixelFontFamily
            )
        }
    }
}