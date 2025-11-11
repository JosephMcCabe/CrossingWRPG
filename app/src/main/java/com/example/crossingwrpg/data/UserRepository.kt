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
            dao.updateName(name)
        }
    }

    suspend fun addWalk(steps: Int) {
        dao.addToTotalSteps(steps)
    }

    suspend fun addSecs(seconds: Int) {
        dao.addToTotalTime(seconds)
    }

    suspend fun addItems(itemsCount: Int) {
        dao.addToTotalItems(itemsCount)
    }

    suspend fun updateSessionItems(items: List<EarnedItem>) {
        dao.updateSessionItems(items)
    }

    suspend fun addRedPotion(count: Int) {
        dao.addRedPotions(count)
    }

    suspend fun addPurplePotion(count: Int) {
        dao.addPurplePotions(count)
    }

    suspend fun consumeRedPotion() {
        dao.consumeRedPotion()
    }

    suspend fun consumePurplePotion() {
        dao.consumePurplePotion()
    }
}