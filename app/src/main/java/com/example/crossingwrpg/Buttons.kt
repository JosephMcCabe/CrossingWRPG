package com.example.crossingwrpg

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DialogButton(
    text: String,
    onClick: ()->Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Black.copy(alpha = 0.8f),
            contentColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        PixelText(
            text,
            fontSize = 25.sp
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: ()->Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(2.dp, Color.Black),
        modifier = Modifier
            .width(150.dp)
            .height(60.dp)
    ) {
        PixelText(
            text,
            fontSize = 30.sp
        )
    }
}