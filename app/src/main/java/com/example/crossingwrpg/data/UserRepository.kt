package com.example.crossingwrpg.data

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

    suspend fun addWalk(steps: Int, seconds: Int) {
        val user = getCurrentUser() ?: return

        val updatedUser = user.copy(
            totalSteps = user.totalSteps + steps,
            totalWalkingSeconds = user.totalWalkingSeconds + seconds,
            speed = 1 + (user.totalSteps.toInt() + steps) / 100
        )
        dao.updateUser(updatedUser)
    }

    suspend fun addDefeatEnemies() {
        dao.addToEnemiesDefeated()
    }
}