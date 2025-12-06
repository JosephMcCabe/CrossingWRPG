package com.example.crossingwrpg

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun NarrativeScreen(
    navController: NavHostController
) {

    MusicPlayer.changeSong("lostshrine")
    MusicPlayer.play()

    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.scroll_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(45.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PixelText(
                "You pause for a moment as you reach the heart of the dark forest. " +
                "Unlike before where the woods were alive with the sounds of small creatures " +
                "and rustling leaves, it is now quiet...",
                fontSize = 40.sp
            )
            Spacer(Modifier.height(16.dp))

            PixelText(
                "You draw your blade, expecting trouble. " +
                        "Out of the corner of your eye a shadow darts behind a tree. " +
                        "A twig snaps, and your eyes gaze ahead on the trail.",
                fontSize = 40.sp,

            )
            Spacer(Modifier.height(16.dp))

            PixelText(
                "Then you see the glint of steel. A goblin jumps out from the brush directly in your path. " +
                        "\"You aren't s'pose to be 'ere, human,\" it says with a wicked grin. " +
                        "\" I'm afraid yer travels end here!",
                fontSize = 40.sp
            )
            Spacer(Modifier.height(8.dp))

            ActionButton(
                text = "Continue",
                onClick = {
                    navController.navigate(Destination.BATTLE.route)
                    MusicPlayer.changeSong("xdeviruchidecisivebattle")
                }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}