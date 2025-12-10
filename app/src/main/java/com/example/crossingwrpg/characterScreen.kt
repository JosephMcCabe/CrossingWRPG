package com.example.crossingwrpg

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.RectangleShape
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
import com.example.crossingwrpg.data.InventoryViewModel
import com.example.crossingwrpg.data.UserViewModel


private data class EquippedEquipment(var rune: Int, var weaponry: Int)

@Composable
fun CharacterScreen(
    navController: NavHostController,
    inventoryVm: InventoryViewModel
) {
    val userVm: UserViewModel = viewModel()
    val user = userVm.userFlow.collectAsState(initial = null).value

    val lightBrown = Color(0xff915f2f)
    val darkBrown = Color(0xff87573C)

    MusicPlayer.pause()
    Column(
        modifier = Modifier
            .offset(y = 0.dp)
            .height(525.dp)
            .width(550.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.forestbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )
        }
    }

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .offset(y = 525.dp)
            .height(265.dp)
            .background(lightBrown)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .border(BorderStroke(19.dp, darkBrown), RectangleShape)
        )

        LazyVerticalGrid(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(23.dp),
            columns = GridCells.Adaptive(minSize = 65.dp),
        ) {
            item { CreateCustomizationItemSlot(0) }
            item { CreateCustomizationItemSlot(-1) }
            item { CreateCustomizationItemSlot(-1) }
            item { CreateCustomizationItemSlot(-1) }

        }
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
                .border(3.dp, Color.Yellow, RoundedCornerShape(12.dp)),
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
                .offset(y = 15.dp),
            contentScale = ContentScale.Fit,
            filterQuality = FilterQuality.None
        )
    }

    var enableCustomizationPopup by remember { mutableStateOf(false) }


    Box(modifier = Modifier
        .fillMaxSize()
        .offset((-102).dp, 25.dp),
        contentAlignment = Alignment.Center
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.customizable_effect),
                contentDescription = "",
                modifier = Modifier
                    .size(73.dp)
                    .offset(x = (-5).dp)
                    .size(75.dp)
                    .clickable(onClick = { if (enableCustomizationPopup == false) {
                        enableCustomizationPopup = true
                    }
                    else {
                        enableCustomizationPopup = false
                    }
                    })
                )
           }
        }
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(102.dp, 25.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.customizable_weaponry),
                contentDescription = "",
                modifier = Modifier
                    .size(73.dp)
                    .clickable(onClick = { if (enableCustomizationPopup == false) {
                        enableCustomizationPopup = true
                    }
                    else {
                        enableCustomizationPopup = false
                    }
                    })
            )
        }
    }

}

@Composable
fun CreateCustomizationItemSlot(id: Int) {
    Image(
        painter = painterResource(R.drawable.customizable_slot),
        contentDescription = "",
        modifier = Modifier
            .size(73.dp)
    )
    if (id >= 0) {
        val drawable = when (id) {
            0 -> R.drawable.pixelsword
            else -> R.drawable.pixelpotion
        }
        CustomizationImage(drawable)
    }
}

@Composable
fun CustomizationImage(id: Int) {
    Image(
        painter = painterResource(id),
        contentDescription = "",
        modifier = Modifier
            .size(64.dp)
            .offset(y = 5.dp)
            .padding(4.dp))
}


