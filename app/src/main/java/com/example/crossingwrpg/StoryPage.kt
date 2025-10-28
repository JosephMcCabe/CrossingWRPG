package com.example.crossingwrpg

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

sealed class BattleState {
    object Start : BattleState()
    object Intro : BattleState()
    object PlayerTurn : BattleState()
    object PlayerAttack: BattleState()
    object PlayerHeal: BattleState()
    object EnemyTurn : BattleState()
    data class End(val winner : String) : BattleState()
}

// represents stats of a character
data class Character(
    val name : String,
    var maxHealth: Int,
    var currentHealth: Int,
    val strength: Int,
    val speed: Int,
    val mind: Int
)

class BattleSimulation(
    val playerState: MutableState<Character>,
    val enemyState: MutableState<Character>
) {
    var state: MutableState<BattleState> = mutableStateOf(BattleState.Start)
        private set

    private val player: Character get() = playerState.value
    private val enemy: Character get() = enemyState.value
    fun advanceBattle() {
        when (state.value) {
            is BattleState.Start -> state.value = BattleState.Intro
            is BattleState.Intro ->firstTurn()

            is BattleState.PlayerAttack -> {
                state.value = checkForWinOrNext(BattleState.EnemyTurn)
            }

            // Heals player and moves to enemy turn
            is BattleState.PlayerHeal-> {
                state.value = BattleState.EnemyTurn
            }

            // attacks player then checks conditions for next state
            is BattleState.EnemyTurn -> {
                enemyAttack()
                state.value = checkForWinOrNext(BattleState.PlayerTurn)
            }

            else -> {}
        }
    }

    // determines the first turn based on who in the turn order is faster
    fun firstTurn() {
        state.value = if (player.speed < enemy.speed) {
            BattleState.EnemyTurn
        }
        else {
            BattleState.PlayerTurn
        }
    }

    fun playerAttack() {
        enemyState.value = enemy.copy(currentHealth = enemy.currentHealth - player.strength)
    }

    fun playerHeal() {
        var newHealth = player.currentHealth + player.mind
        if (newHealth > player.maxHealth) {
            newHealth = player.maxHealth
        }
        playerState.value = player.copy(currentHealth = newHealth)
    }

    fun enemyAttack() {
        playerState.value = player.copy(currentHealth = player.currentHealth - enemy.strength)
    }

    fun checkForWinOrNext(nextState: BattleState): BattleState {
        return when {
            enemy.currentHealth <= 0 -> {
                BattleState.End("${player.name} defeated ${enemy.name}!")
            }
            player.currentHealth <= 0 -> {
                BattleState.End("${player.name} was defeated by ${enemy.name}...")
            }
            else -> nextState
        }
    }

    fun chooseAction(action: BattleState) {
        when (action) {
            is BattleState.PlayerAttack -> {
                this.playerAttack()
                state.value = BattleState.PlayerAttack
            }
            is BattleState.PlayerHeal -> {
                this.playerHeal()
                state.value = BattleState.PlayerHeal
            }
            else -> {}
        }
    }

    fun reset() {
        playerState.value = Character("Joey", 100, 100,25, 11, 15)
        enemyState.value = Character("Evil Goblin thing",150, 150, 15, 5, 0)
        state.value = BattleState.Start
    }
}

@Composable
fun CharacterHealthBar(character: Character, modifier: Modifier = Modifier, isPlayer: Boolean) {
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

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${character.name}: ${character.currentHealth.coerceAtLeast(0)}/${character.maxHealth} HP",
            fontFamily = pixelFontFamily,
            fontSize = 30.sp,
        )
        Spacer(Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
            color = healthBarColor,
            trackColor = trackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
    }
}

@Composable
fun BattleScreen(onNavigateToHome: () -> Unit) {

    val playerState = remember { mutableStateOf(Character("Joey", 100, 100, 25, 11, 15)) }
    val enemyState = remember { mutableStateOf(Character("Evil Goblin thing", 150, 150, 15, 5, 0)) }

    // remember battle simulation
    val battleSimulation = remember {
        BattleSimulation(
            playerState = playerState,
            enemyState = enemyState
        )
    }

    val player by playerState
    val enemy by enemyState
    val state by battleSimulation.state

    fun nextTurn() {
        battleSimulation.advanceBattle()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
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
                    .weight(2f)
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pixelgoblin),
                    contentDescription = "Goblin Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
            }
            CharacterHealthBar(
                character = player,
                isPlayer = true
            )

            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 8.dp),
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
                        is BattleState.Start -> PixelText(
                            text = "You have entered a battle"
                        )

                        is BattleState.Intro -> PixelText(
                            text = "You are attacked by a ${enemy.name}!"
                        )

                        is BattleState.PlayerTurn -> PixelText(
                            text = "Your turn!"
                        )

                        is BattleState.PlayerAttack -> PixelText(
                            text = "You attacked ${enemy.name} for ${player.strength} damage!",
                        )

                        is BattleState.PlayerHeal -> PixelText(
                            text = "You healed for ${player.mind} HP!"
                        )


                        is BattleState.EnemyTurn -> PixelText(
                            text = "You are attacked by the ${enemy.name} for ${enemy.strength} damage!",
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
                                    battleSimulation.chooseAction(BattleState.PlayerHeal)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Green,
                                    contentColor = Color.Black
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.pixelpotion),
                                        contentDescription = "Attack Icon",
                                        modifier = Modifier.height(25.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    PixelText(
                                        text = "Heal"
                                    )
                                }
                            }
                        }
                    }

                    is BattleState.End -> {
                        Button(
                            onClick = {
                                battleSimulation.reset()
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