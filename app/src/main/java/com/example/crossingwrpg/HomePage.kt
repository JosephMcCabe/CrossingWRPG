package com.example.crossingwrpg

import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// A main menu screen displaying the character, game title, and navigation bar.
@Composable
fun HomePage(onNavigateToStory: () -> Unit) {
    val userVm: com.example.crossingwrpg.data.UserViewModel = viewModel()
    val needsName by userVm.needsName.collectAsState()
    val user by userVm.userFlow.collectAsState()

    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    LaunchedEffect(Unit) {
        requestPermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        )
    }

    if (needsName) {
        NameDialog(onConfirm = { name -> userVm.saveName(name) })
    }

    // Box used to stack main character image, title, and buttons
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 60.dp),
                painter = painterResource(R.drawable.samplecharacter),
                contentDescription = null,
                contentScale = ContentScale.FillHeight

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
        Text(
            text = "Hi, ${user?.name ?: "Walker"}",
            fontFamily = pixelFontFamily,
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            color = Color.DarkGray,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 76.dp)
                .padding(16.dp)
        )
    }
}

@Composable
private fun NameDialog(
    onConfirm: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    val canConfirm = text.isNotBlank()

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Welcome!") },
        text = {
            Column {
                Text("What is your name?")
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    placeholder = { Text("Your name") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { if (canConfirm) onConfirm(text) }
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = canConfirm,
                onClick = { onConfirm(text) }
            ) { Text("Save") }
        }
    )
}