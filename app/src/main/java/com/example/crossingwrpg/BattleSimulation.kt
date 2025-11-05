package com.example.crossingwrpg

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class BattleSimulation {
    val stepsPerSpeedPoint = 10
    var totalSteps: Long by mutableLongStateOf(0L)
        private set
    var walkSpeed: Int by mutableIntStateOf(1)
        private set
    var stepsSinceLastSpeedIncrease: Long by mutableLongStateOf(0L)
        private set

    val baseMaxHealth: Int = 100
    val baseStrength: Int = 25
    val baseMind: Int = 15

    var playerState: MutableState<Character> = mutableStateOf(createPlayerCharacter())
        private set
    var enemyState: MutableState<Character> = mutableStateOf(createEnemyCharacter())
        private set
    var battleState: MutableState<BattleState> = mutableStateOf(BattleState.Start)
        private set

    val playerStrength: Int get() = playerState.value.strength
    val enemyStrength: Int get() = enemyState.value.strength

    private val player: Character get() = playerState.value
    private val enemy: Character get() = enemyState.value

    fun createPlayerCharacter(): Character {
        return Character(
            name = "Fatima",
            maxHealth = baseMaxHealth,
            currentHealth = baseMaxHealth,
            strength = baseStrength,
            speed = walkSpeed,
            mind = baseMind
        )
    }

    fun createEnemyCharacter(): Character {
        return Character(
            name = "Evil Goblin Thing",
            maxHealth = 150,
            currentHealth = 150,
            strength = 15,
            speed = 2,
            mind = 0
        )
    }

    fun updateSteps(newTotalSteps: Long) {
        if(newTotalSteps <= totalSteps) {
            return
        }

        val stepsGained = newTotalSteps - totalSteps
        totalSteps = newTotalSteps

        val stepsTowardsNext = stepsSinceLastSpeedIncrease + stepsGained
        val speedIncrease = (stepsTowardsNext / stepsPerSpeedPoint).toInt()

        var speedChanged = false

        if(speedIncrease > 0) {
            walkSpeed += speedIncrease
            speedChanged = true
        }

        if(speedChanged || player.speed != walkSpeed) {
            playerState.value = player.copy(speed = walkSpeed)
        }
    }

    fun advanceBattle() {
        when (battleState.value) {
            is BattleState.Start -> battleState.value = BattleState.Intro
            is BattleState.Intro -> firstTurn()

            is BattleState.PlayerAttack -> {
                battleState.value = checkForWinOrNext(BattleState.EnemyTurn)
            }

            is BattleState.PlayerHeal -> {
                battleState.value = checkForWinOrNext(BattleState.EnemyTurn)
            }

            is BattleState.EnemyTurn -> {
                enemyAttack()
                battleState.value = checkForWinOrNext(BattleState.PlayerTurn)
            }
            else -> {}
        }
    }

    fun firstTurn() {
        battleState.value = if(player.speed < enemy.speed) {
            BattleState.EnemyTurn
        } else {
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
                BattleState.End("${player.name} defeated ${enemy.name}...")
            }
            player.currentHealth <= 0 -> {
                BattleState.End("${player.name} was defeated by ${enemy.name}... ")
            }
            else -> nextState
        }
    }

    fun chooseAction(action: BattleState) {
        when (action) {
            is BattleState.PlayerAttack -> {
                this.playerAttack()
                battleState.value = BattleState.PlayerAttack
            }
            is BattleState.PlayerHeal -> {
                this.playerHeal()
                battleState.value = BattleState.PlayerHeal
            }
            else -> {}
        }
    }

    fun resetBattle() {
        playerState.value = createPlayerCharacter()
        enemyState.value = createEnemyCharacter()
        battleState.value = BattleState.Start
    }
}