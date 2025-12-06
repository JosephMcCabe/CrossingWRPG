package com.example.crossingwrpg

import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.crossingwrpg.data.UserViewModel

@Composable
fun HomePage() {
    val userVm: UserViewModel = viewModel()
    val needsName by userVm.needsName.collectAsState()
    val user by userVm.userFlow.collectAsState()

    MusicPlayer.pause()

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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.background_layer_1),
            contentDescription = null,
            modifier = Modifier
                .requiredHeight(650.dp)
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(R.drawable.background_layer_2),
            contentDescription = null,
            modifier = Modifier
                .requiredHeight(650.dp)
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(R.drawable.background_layer_3),
            contentDescription = null,
            modifier = Modifier
                .requiredHeight(650.dp)
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.pixelsoldier)
                    .decoderFactory(ImageDecoderDecoder.Factory())
                    .build(),
                contentDescription = "Soldier Sprite",
                modifier = Modifier
                    .size(500.dp)
                    .offset(x = 0.dp, y = 180.dp),
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.None
            )
        }

        Card(
            modifier = Modifier
                .padding(1.dp)
                .height(100.dp)
                .offset(y = 15.dp)
                .border(1.dp, Color.White, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Crossing",
                    fontFamily = pixelFontFamily,
                    textAlign = TextAlign.Center,
                    fontSize = 70.sp,
                    color = Color.LightGray
                )
                Text(
                    text = "A Walking RPG",
                    fontFamily = pixelFontFamily,
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    color = Color.LightGray
                )
            }
        }
        Text(
            text = "Hi, ${user?.name ?: "Walker"}",
            fontFamily = pixelFontFamily,
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
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