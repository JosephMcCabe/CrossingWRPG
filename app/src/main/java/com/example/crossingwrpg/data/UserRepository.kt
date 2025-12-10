package com.example.crossingwrpg.data

import android.content.Context
import com.example.crossingwrpg.AchievementTracker

class UserRepository(private val dao: UserDao, private val context: Context) {

    private val achievementTracker by lazy { AchievementTracker(context) }

    val userFlow = dao.observeById(1)
    fun observeUser() = dao.observeById(1)

    suspend fun getCurrentUser(): User? {
        return dao.getCurrentUser()
    }

    suspend fun updateUser(user: User) {
        dao.updateUser(user)
        checkAchievements()
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

    suspend fun addWalk(steps: Int, seconds: Int) {
        val user = getCurrentUser() ?: return

        val updatedUser = user.copy(
            totalSteps = user.totalSteps + steps,
            totalWalkingSeconds = user.totalWalkingSeconds + seconds,
            speed = 1 + (user.totalSteps.toInt() + steps) / 100
        )
        dao.updateUser(updatedUser)
        checkAchievements()
    }

    suspend fun addDefeatEnemies() {
        dao.addToEnemiesDefeated()
        checkAchievements()
    }

    private suspend fun checkAchievements() {
        val user = getCurrentUser() ?: return
        achievementTracker.checkAchievements(
            steps = user.totalSteps.toInt(),
            enemiesDefeated = user.enemiesDefeated,
            walkingSeconds = user.totalWalkingSeconds
        )
    }
}