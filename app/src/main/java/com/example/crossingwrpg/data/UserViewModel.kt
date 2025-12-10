package com.example.crossingwrpg.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy {
        val db = AppDatabase.getDatabase(app)
        UserRepository(db.userDao(), app.applicationContext)
    }

    val userFlow: StateFlow<User?> = repo.userFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val needsName: StateFlow<Boolean> = userFlow
        .map { it?.name.isNullOrBlank() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            true
        )

    fun saveName(name: String) = viewModelScope.launch {
        repo.createOrUpdateName(name.trim())
    }

    fun recordWalk(steps: Int, seconds: Int) = viewModelScope.launch {
        repo.addWalk(steps, seconds)
    }

    fun updateDefeatedEnemies() = viewModelScope.launch {
        repo.addDefeatEnemies()
    }
}