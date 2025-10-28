package com.example.crossingwrpg

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp

// state machine for battle
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
    // rename stats
    val name : String,
    var vit: Int,
    val str: Int,
    val spd: Int,
    val mnd: Int
)

// placeholder values which will soon be replaced with data from a database
private var player = Character("Joey", 100, 25, 11, 15)
private var enemy = Character("Evil Goblin thing",150, 15, 5, 0)

// simulation that controls the state machine and executes logic
class BattleSimulation {
    var state: BattleState = BattleState.Start
        private set

    // handles state changes
    fun advanceBattle() {
        when (state) {
            // start and introduction dialogue
            is BattleState.Start -> state = BattleState.Intro
            is BattleState.Intro ->firstTurn()

            // on player attack, check for win or next state
            is BattleState.PlayerAttack -> {
                state = checkForWinOrNext(BattleState.EnemyTurn)
            }

            // Heals player and moves to enemy turn
            is BattleState.PlayerHeal-> {
                playerHeal()
                state = BattleState.EnemyTurn
            }

            // attacks player then checks conditions for next state
            is BattleState.EnemyTurn -> {
                enemyAttack()
                state = checkForWinOrNext(BattleState.PlayerTurn)
            }

            else -> {}
        }
    }

    // determines the first turn based on who in the turn order is faster
    fun firstTurn() {
        state = if (player.spd < enemy.spd) {
            BattleState.EnemyTurn
        }
        else {
            BattleState.PlayerTurn
        }
    }

    // player attacks, subtract enemy hp
    fun playerAttack() {
        enemy.vit -= player.str
    }

    // player heals, cannot go above max health
    fun playerHeal() {
        player.vit += player.mnd
        if (player.vit > 100) {
            player.vit = 100
        }
    }

    // enemy attacks, subtract player hp
    fun enemyAttack() {
        player.vit -= enemy.str
    }

    // checks if there is a winner and sets end state accordingly, or moves on to the next state
    fun checkForWinOrNext(nextState: BattleState): BattleState {
        return when {
            enemy.vit <= 0 -> {
                BattleState.End("${player.name} defeated ${enemy.name}!")
            }
            player.vit <= 0 -> {
                BattleState.End("${player.name} was defeated by ${enemy.name}...")
            }
            else -> nextState
        }
    }

    // allows for branching options for player to choose approach
    fun chooseAction(action: BattleState) {
        when (action) {
            is BattleState.PlayerAttack -> {
                this.playerAttack()
                state = BattleState.PlayerAttack
            }
            is BattleState.PlayerHeal -> {
                this.playerHeal()
                state = BattleState.PlayerHeal
            }
            else -> {}
        }
    }

    fun reset() {
        player = Character("Joey", 100, 25, 11, 15)
        enemy = Character("Evil Goblin thing",150, 15, 5, 0)
    }
}

@Composable
fun BattleScreen(onNavigateToHome: () -> Unit) {
    // remember battle simulation
    val battleSimulation = remember { BattleSimulation() }

    var state by remember { mutableStateOf<BattleState>(BattleState.Start) }

    fun nextTurn() {
        battleSimulation.advanceBattle()
        state = battleSimulation.state
    }
    //Box
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            when (state) {
                is BattleState.Start -> Text(
                    text = "You have entered a battle",
                    fontFamily = pixelFontFamily,
                    fontSize = 20.sp
                )

                is BattleState.Intro -> Text(
                    text = "You are attacked by a ${enemy.name}!",
                    fontFamily = pixelFontFamily,
                    fontSize = 20.sp
                )

                is BattleState.PlayerTurn -> Text(
                    text = "Your turn!",
                    fontFamily = pixelFontFamily,
                    fontSize = 20.sp
                )

                is BattleState.PlayerAttack -> Text(
                    text = "You attacked ${enemy.name} for ${player.str} damage!",
                    fontFamily = pixelFontFamily,
                    fontSize = 20.sp
                )

                is BattleState.PlayerHeal -> Text(
                    text = "You healed for ${player.mnd} HP!",
                    fontFamily = pixelFontFamily,
                    fontSize = 20.sp
                )


                is BattleState.EnemyTurn -> Text(
                    text = "You are attacked by the ${enemy.name} for ${enemy.str} damage!",
                    fontFamily = pixelFontFamily,
                    fontSize = 20.sp
                )

                is BattleState.End -> {
                    val endState = state as BattleState.End
                    Text(
                        text = endState.winner,
                        fontFamily = pixelFontFamily,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            when (state) {
                is BattleState.Start,
                is BattleState.Intro,
                is BattleState.PlayerAttack,
                is BattleState.PlayerHeal,
                is BattleState.EnemyTurn -> {
                    Button(onClick = { nextTurn() }) { Text("Next") }
                }

                is BattleState.PlayerTurn -> {
                    Column {
                        Button(onClick = { battleSimulation.chooseAction(BattleState.PlayerAttack); state = battleSimulation.state }) {
                            Text("Attack")
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { battleSimulation.chooseAction(BattleState.PlayerHeal); state = battleSimulation.state }) {
                            Text("Heal")
                        }
                    }
                }

                is BattleState.End -> {
                    Button(onClick = {
                        battleSimulation.reset()
                        state = battleSimulation.state
                        onNavigateToHome()
                    }) { Text("Return Home") }
                }
            }
        }
    }
}