package com.example.crossingwrpg

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.crossingwrpg.data.UserViewModel

@Composable
fun CharacterScreen(
    navController: NavHostController
) {
    val userVm: UserViewModel = viewModel()
    val user = userVm.userFlow.collectAsState(initial = null).value

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.forestbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(IntrinsicSize.Min)
                .border(3.dp, Color.Green, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                PixelText("Character", fontSize = 42.sp)
                Spacer(
                    Modifier.height(16.dp)
                )
                PixelText("Name: ${user?.name ?: "Walker"}")
                PixelText("Vitality: ${user?.vitality ?: 0}")
                PixelText("Strength: ${user?.strength ?: 0}")
                PixelText("Speed: ${user?.speed ?: 0}")
                PixelText("Mind: ${user?.mind ?: 0}")
                PixelText("Total Steps: ${user?.totalSteps ?: 0}")
                PixelText("Walking Time: ${user?.totalWalkingSeconds ?: 0}")

            }
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.pixelsoldier)
                .decoderFactory(ImageDecoderDecoder.Factory())
                .build(),
            contentDescription = "Goblin Image",
            modifier = Modifier
                .size(180.dp)
                .offset(y = 230.dp),
            contentScale = ContentScale.Fit,
            filterQuality = FilterQuality.None
        )
    }
}

