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

@Composable
fun WalkMapScreen(onNavigateToWalk: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.pathimage),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
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
                    Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        )
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

        Button(
            onClick = onNavigateToWalk,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 70.dp)
                .size(93.dp)
        ) {
            Text(
                text = "Home",
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                fontFamily = pixelFontFamily
            )
        }

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
            Text(
                text = "Start Walking",
                fontSize = 27.sp,
                textAlign = TextAlign.Center,
                fontFamily = pixelFontFamily
            )
        }
    }
}