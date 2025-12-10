package com.example.crossingwrpg

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.crossingwrpg.data.User

class BattleSimulation {
    var playerState: MutableState<Character> = mutableStateOf(createPlayerCharacter())
        private set
    var enemyState: MutableState<Character> = mutableStateOf(createEnemyCharacter())
        private set
    var battleState: MutableState<BattleState> = mutableStateOf(BattleState.Start)
        private set

    var lastPlayerDamage: MutableState<Int> = mutableStateOf(0)
        private set
    var lastEnemyDamage: MutableState<Int> = mutableStateOf(0)
        private set

    private val player: Character get() = playerState.value
    private val enemy: Character get() = enemyState.value

    fun createPlayerCharacter(): Character {
        return Character(
            name = "Walker",
            maxHealth = 100,
            currentHealth = 100,
            strength = 100,
            speed = 1,
            mind = 1
        )
    }

    private fun userToCharacter(user: User) = Character(
        name = user.name.ifBlank { "Walker" },
        maxHealth = user.vitality,
        currentHealth = user.vitality,
        strength = user.strength,
        speed = user.speed,
        mind = user.mind
    )

    fun applyUser(user: User) {
        playerState.value = userToCharacter(user)
    }

    fun createEnemyCharacter(): Character {
        return Character(
            name = "Evil Goblin Thing",
            maxHealth = 75,
            currentHealth = 75,
            strength = 15,
            speed = 2,
            mind = 0
        )
    }

    private fun randomDamage(base: Int): Int {
        return base + (0..10).random()
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
        val damage = randomDamage(player.strength)
        lastPlayerDamage.value = damage
        enemyState.value = enemy.copy(currentHealth = enemy.currentHealth - damage)
    }

    fun playerHeal() {
        var newHealth = player.currentHealth + 25
        if (newHealth > player.maxHealth) {
            newHealth = player.maxHealth
        }
        playerState.value = player.copy(currentHealth = newHealth)
    }

    fun enemyAttack() {
        val damage = randomDamage(enemy.strength)
        lastEnemyDamage.value = damage
        playerState.value = player.copy(currentHealth = player.currentHealth - damage)
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