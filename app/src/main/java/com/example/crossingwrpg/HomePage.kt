package com.example.crossingwrpg

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomePage(onNavigateToHome: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Adventurer!",
                fontSize = 40.sp,
                fontFamily = pixelFontFamily
            )
        }

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

        Button(
            onClick = {/*story*/ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 70.dp)
                .size(93.dp)
        ) {
            Text(
                text = "Story",
                fontSize = 22.sp,
                fontFamily = pixelFontFamily,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = onNavigateToHome,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black.copy(alpha = 0.9f)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .size(120.dp)
        ) {
            Text(
                text = "Walk",
                fontSize = 30.sp,
                fontFamily = pixelFontFamily,
                textAlign = TextAlign.Center
            )
        }
    }
}