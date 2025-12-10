package com.example.crossingwrpg.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _user = MutableStateFlow<User?>(null)
    val userFlow: StateFlow<User?> = _user

    val needsName: StateFlow<Boolean> = _user
        .map { user ->
            user?.name.isNullOrBlank() }
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

    init {
        viewModelScope.launch {
            repo.userFlow.collect { dbUser ->
                _user.value = dbUser
            }
        }
    }
}