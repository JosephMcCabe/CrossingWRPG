package com.example.crossingwrpg.data

class UserRepository(private val dao: UserDao) {
    fun observeUser() = dao.observeById(1)

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
}