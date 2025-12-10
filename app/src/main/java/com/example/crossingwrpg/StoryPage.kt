package com.example.crossingwrpg

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.FilterQuality
import com.example.crossingwrpg.data.UserViewModel
import com.example.crossingwrpg.data.InventoryViewModel
import coil.decode.ImageDecoderDecoder
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

sealed class BattleState {
    object Start : BattleState()
    object Intro : BattleState()
    object PlayerTurn : BattleState()
    object PlayerAttack: BattleState()
    object PlayerHeal: BattleState()
    object EnemyTurn : BattleState()
    data class End(val winner : String) : BattleState()
}

data class Character(
    val name : String,
    var maxHealth: Int,
    var currentHealth: Int,
    val strength: Int,
    val speed: Int,
    val mind: Int
)

@Composable
fun CharacterHealthBar(
    character: Character,
    modifier: Modifier = Modifier,
    isPlayer: Boolean
) {
    val progress = character.currentHealth.toFloat().coerceAtLeast(0f) / character.maxHealth.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue =  progress,
        animationSpec = tween(durationMillis = 300),
        label = "HealthAnimation"
    )
    val healthBarColor = when (isPlayer) {
        true -> Color.Green
        false -> Color.Red
    }
    val trackColor = Color.Gray.copy(alpha = 0.3f)

    Card(
        modifier = Modifier
            .height(80.dp)
            .width(600.dp)
            .border(0.5.dp, Color.Transparent, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlayer) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${character.name}: ${character.currentHealth.coerceAtLeast(0)}/${character.maxHealth} HP",
                fontFamily = pixelFontFamily,
                fontSize = 30.sp,
                color = if (isPlayer) Color.Black else Color.White
            )
            Spacer(Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(12.dp),
                color = healthBarColor,
                trackColor = trackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }
    }
}


var battleWon : Boolean = false

@Composable
fun BattleScreen(
    battleSimulation: BattleSimulation,
    onNavigateToHome: () -> Unit,
    userVm: UserViewModel,
    inventoryVm: InventoryViewModel
) {

    DisposableEffect(Unit) {
        onDispose {
            MusicPlayer.free()
            battleSimulation.resetBattle()
        }
    }

    val player by battleSimulation.playerState
    val enemy by battleSimulation.enemyState
    val state by battleSimulation.battleState
    val lastPlayerDamage by battleSimulation.lastPlayerDamage
    val lastEnemyDamage by battleSimulation.lastEnemyDamage

    val user by userVm.userFlow.collectAsState(initial = null)

    LaunchedEffect(user?.uid, state) {
        if (user != null && (state is BattleState.Start || state is BattleState.Intro)) {
            battleSimulation.applyUser(user!!)
        }
    }

    val context = LocalContext.current
    val redPotionAvailable by inventoryVm.healthPotionQuantity.collectAsState()

    LaunchedEffect(Unit) {
        inventoryVm.loadHealthPotionQuantity()
    }

    fun nextTurn() {
        battleSimulation.advanceBattle()
    }
    val Tan = Color(0xFFD2B48C)
    Box(
        modifier = Modifier
            .fillMaxSize().background(Tan)
    )

    Image(
        painter = painterResource(R.drawable.background_layer_1),
        contentDescription = null,
        modifier = Modifier
            .requiredHeight(450.dp)
            .fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Image(
        painter = painterResource(R.drawable.background_layer_2),
        contentDescription = null,
        modifier = Modifier
            .requiredHeight(450.dp)
            .fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Image(
        painter = painterResource(R.drawable.background_layer_3),
        contentDescription = null,
        modifier = Modifier
            .requiredHeight(450.dp)
            .fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .fillMaxHeight()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        )
        {
            CharacterHealthBar(
                character = enemy,
                isPlayer = false
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2.5f)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.pixelgoblin)
                        .decoderFactory(ImageDecoderDecoder.Factory())
                        .build(),
                    contentDescription = "Goblin Image",
                    modifier = Modifier
                        .offset(x = 20.dp, y = 90.dp)
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    filterQuality = FilterQuality.High
                )
            }
            CharacterHealthBar(
                character = player,
                isPlayer = true
            )

            Spacer(Modifier.height(10.dp))
            val TransparentDarkBrown = Color(0x805C4033).copy(alpha = 0.8f)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TransparentDarkBrown,
                    contentColor = Color.White
                ),

                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (state) {
                        is BattleState.Start -> {
                            PixelText(
                                text = "You have entered a battle"
                            )

                            battleWon = false
                            MusicPlayer.preparePlayer(context)
                            MusicPlayer.changeSong("xdeviruchidecisivebattle")
                            MusicPlayer.loop()

                        }

                        is BattleState.Intro -> {
                            MusicPlayer.play()
                            PixelText(
                                text = "You are attacked by a ${enemy.name}!"
                            )
                        }

                        is BattleState.PlayerTurn -> PixelText(
                            text = "Your turn!"
                        )

                        is BattleState.PlayerAttack -> PixelText(
                            text = "You attacked ${enemy.name} for $lastPlayerDamage damage!",
                        )

                        is BattleState.PlayerHeal -> PixelText(
                            text = "You healed for ${player.mind} HP!"
                        )

                        is BattleState.EnemyTurn -> PixelText(
                            text = "You are attacked by the ${enemy.name} for $lastEnemyDamage damage!",
                        )

                        is BattleState.End -> {
                            val endState = state as BattleState.End
                            PixelText(
                                text = endState.winner
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (state) {
                    is BattleState.Start,
                    is BattleState.Intro,
                    is BattleState.PlayerAttack,
                    is BattleState.PlayerHeal,
                    is BattleState.EnemyTurn -> {
                        Button(
                            onClick = { nextTurn() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            PixelText(
                                text = "Next"
                            )
                        }
                    }

                    is BattleState.PlayerTurn -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    battleSimulation.chooseAction(BattleState.PlayerAttack)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.Black
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.pixelsword),
                                        contentDescription = "Attack Icon",
                                        modifier = Modifier.height(25.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))

                                    PixelText(
                                        text = "Attack"
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    inventoryVm.useHealthPotion()
                                    battleSimulation.chooseAction(BattleState.PlayerHeal)
                                },
                                enabled = redPotionAvailable > 0,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Green,
                                    contentColor = Color.Black
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.healthpotion),
                                        contentDescription = "Attack Icon",
                                        modifier = Modifier.height(25.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    PixelText(
                                        text = "Heal (x$redPotionAvailable)"
                                    )
                                }
                            }
                        }
                    }

                    is BattleState.End -> {
                        if (battleWon == false) { // Runs once when on battle end
                            userVm.updateDefeatedEnemies()
                            MusicPlayer.changeSong("victory1")
                            MusicPlayer.unloop()
                        }
                        battleWon = true

                        Button(
                            onClick = {
                                MusicPlayer.pause()
                                onNavigateToHome()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            PixelText(
                                text = "Return Home"
                            )
                        }
                    }
                }
            }
        }
    }
}