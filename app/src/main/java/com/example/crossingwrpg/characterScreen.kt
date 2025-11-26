package com.example.crossingwrpg

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.crossingwrpg.data.UserViewModel

private var customizationName = "Default"

private data class characterEquip(var helmet: Int, var chestplate: Int, var bottoms: Int, var boots: Int, var weaponry: Int)

@Composable
fun CharacterScreen(
    navController: NavHostController
) {
    val userVm: UserViewModel = viewModel()
    val user = userVm.userFlow.collectAsState(initial = null).value

    MusicPlayer.pause()

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

    var enableCustomizationPopup by remember { mutableStateOf(false) }


    Box(modifier = Modifier
        .fillMaxSize()
        .offset(15.dp, 105.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .border(5.dp, Color.Black, RoundedCornerShape(12.dp))
                .size(75.dp)
                .clickable(onClick = { if (enableCustomizationPopup == false) {
                    customizationName = "Helmets"
                    enableCustomizationPopup = true
                }
                else {
                    enableCustomizationPopup = false
                }
                })
            )
            Box(modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .border(5.dp, Color.Black, RoundedCornerShape(12.dp))
                .size(75.dp).clickable(onClick = { if (enableCustomizationPopup == false) {
                    customizationName = "Chestplates"
                    enableCustomizationPopup = true
                }
                else {
                    enableCustomizationPopup = false
                }
                })
            )
            Box(modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .border(5.dp, Color.Black, RoundedCornerShape(12.dp))
                .size(75.dp).clickable(onClick = { if (enableCustomizationPopup == false) {
                    customizationName = "Pants"
                    enableCustomizationPopup = true
                }
                else {
                    enableCustomizationPopup = false
                }
                })
            )
            Box(modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .border(5.dp, Color.Black, RoundedCornerShape(12.dp))
                .size(75.dp)
                .clickable(onClick = { if (enableCustomizationPopup == false) {
                    customizationName = "Shoes"

                    enableCustomizationPopup = true
                }
                else {
                    enableCustomizationPopup = false
                }
                })
            ) {

            }
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(-15.dp, 95.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Box(modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .border(5.dp, Color.Black, RoundedCornerShape(12.dp))
                .size(75.dp)
                .clickable(onClick = { if (enableCustomizationPopup == false) {
                    customizationName = "Weaponry"
                    enableCustomizationPopup = true
                }
                else {
                    enableCustomizationPopup = false
                }
                })
            )
        }
    }

    if (enableCustomizationPopup) {
        CustomizationPopup(customizationName)
    }
}

@Composable
fun CreateCustomizationItem(id: Int) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(Color.LightGray)
        .border(5.dp, Color.Black, RoundedCornerShape(12.dp))
        .size(75.dp)
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
        contentDescription = "test",
        modifier = Modifier
            .size(64.dp)
            .padding(4.dp))
}

@Composable
fun CustomizationPopup(titleName: String) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(5.dp, Color.Black, RoundedCornerShape(12.dp))
                .size(225.dp, height = 310.dp),
        ) {
            Text(
                text = titleName,
                textAlign = TextAlign.Center,
                fontFamily = pixelFontFamily,
                fontSize = 43.sp,
                modifier = Modifier
                    .padding(top = 18.dp)
                    .align(Alignment.TopCenter)
            )
            LazyVerticalGrid(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 95.dp),
                columns = GridCells.Adaptive(minSize = 75.dp),
            ) {
                if (titleName == "Helmets") {
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                }
                if (titleName == "Chestplates") {
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                }

                if (titleName == "Pants") {
                    item { CreateCustomizationItem(-1) }
                }

                if (titleName == "Shoes") {
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                }
                if (titleName == "Weaponry") {
                    item { CreateCustomizationItem(0) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                    item { CreateCustomizationItem(-1) }
                }

            }
        }
    }
}

