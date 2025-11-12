package com.example.crossingwrpg.data

import com.example.crossingwrpg.EarnedItem

class UserRepository(private val dao: UserDao) {

    val userFlow = dao.observeById(1)
    fun observeUser() = dao.observeById(1)

    suspend fun getCurrentUser(): User? {
        return dao.getCurrentUser()
    }

    suspend fun updateUser(user: User) {
        dao.updateUser(user)
    }

    suspend fun getUser() = dao.getById(1)
    suspend fun createOrUpdateName(name: String) {
        val current = dao.getById(1)
        if (current == null) {
            dao.insert(User(uid = 1, name = name))
        } else {
            dao.updateUser(current.copy(name = name))
        }
    }

    suspend fun addWalk(steps: Int, seconds: Int, earnedItems: List<EarnedItem>) {
        val user = getCurrentUser() ?: return

        val redPotionCount = earnedItems.filter { it.id == "red_potion"}.sumOf { it.count }
        val purplePotionCount = earnedItems.filter { it.id == "purple_potion"}.sumOf { it.count }
        val totalItemsCount = earnedItems.sumOf { it.count }

        val updatedUser = user.copy(
            totalSteps = user.totalSteps + steps,
            totalWalkingSeconds = user.totalWalkingSeconds + seconds,
            speed = user.totalSteps.toInt() / 10,
            totalItems = user.totalItems + totalItemsCount,
            sessionItems = earnedItems
        )

        val finalUser = updatedUser.copy(
            redPotions = if (redPotionCount > 0) {
                updatedUser.redPotions + redPotionCount
            } else {
                updatedUser.redPotions
            },
            purplePotions = if (purplePotionCount > 0) {
                updatedUser.purplePotions + purplePotionCount
            } else {
                updatedUser.purplePotions
            }
        )
        dao.updateUser(finalUser)
    }

    suspend fun consumeRedPotion() {
        dao.consumeRedPotion()
    }

    suspend fun consumePurplePotion() {
        dao.consumePurplePotion()
    }

    suspend fun addDefeatEnemies() {
        dao.addToEnemiesDefeated()
    }
}