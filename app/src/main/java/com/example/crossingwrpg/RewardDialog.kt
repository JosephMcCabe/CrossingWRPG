package com.example.crossingwrpg

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun RewardDialog(
    totalItemCount: Int,
    onDismiss: () -> Unit
) {
    val message = if (totalItemCount == 1) {
        "As you were walking you picked up 1 item!"
    } else {
        "As you were walking you came across $totalItemCount items!"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
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
            DialogButton(
                text = "OK",
                onClick = onDismiss
            )
        }
    )
}