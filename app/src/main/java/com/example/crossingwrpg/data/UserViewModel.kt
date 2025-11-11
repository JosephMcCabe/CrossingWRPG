package com.example.crossingwrpg.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.crossingwrpg.EarnedItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy {
        val db = AppDatabase.getDatabase(app)
        UserRepository(db.userDao())
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

    fun recordWalk(steps: Int, seconds:Int, earnedItems: List<EarnedItem>) = viewModelScope.launch {

        val redPotionCount = earnedItems.filter { it.id == "red_potion"}.sumOf { it.count }
        val purplePotionCount = earnedItems.filter { it.id == "purple_potion"}.sumOf { it.count }

        repo.addWalk(steps)
        repo.addSecs(seconds)

        if ((redPotionCount > 0) && (redPotionCount < 50)) {
            repo.addRedPotion(redPotionCount)
        }

        if ((purplePotionCount > 0) && (purplePotionCount < 50)) {
            repo.addPurplePotion(purplePotionCount)
        }

        val totalCount = earnedItems.sumOf { it.count }
        if (totalCount > 0) {
            repo.addItems(totalCount)
        }

        if (earnedItems.isNotEmpty()) {
            repo.updateSessionItems(earnedItems)
        }
    }
    fun useRedPotion() = viewModelScope.launch {
        repo.consumeRedPotion()
    }

    fun usePurplePotion() = viewModelScope.launch {
        repo.consumePurplePotion()
    }
}