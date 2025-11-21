package com.example.crossingwrpg.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy {
        val db = AppDatabase.getDatabase(app)
        InventoryRepository(db.inventoryDao())
    }
    private val _healthPotionQuantity = MutableStateFlow(0)
    val healthPotionQuantity: StateFlow<Int> = _healthPotionQuantity.asStateFlow()

    private val _sessionEarnedItems = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val sessionEarnedItem: StateFlow<Map<Long, Int>> = _sessionEarnedItems

    private val _allItems = MutableStateFlow<List<Item>>(emptyList())
    val allItems: StateFlow<List<Item>> =_allItems

    init {
        loadHealthPotionQuantity()
        loadAllItems()
    }

    fun loadHealthPotionQuantity() = viewModelScope.launch {
        _healthPotionQuantity.value = repo.getQuantity(1L)
    }

    fun useHealthPotion() = viewModelScope.launch {
        repo.consumeItem(1L)
        loadHealthPotionQuantity()
    }

    fun addEarnedItem(itemId: Long, count: Int = 1) {
        val currentItems = _sessionEarnedItems.value.toMutableMap()
        val currentCount = currentItems[itemId] ?: 0
        currentItems[itemId] = currentCount + count
        _sessionEarnedItems.value = currentItems
    }

    fun commitEarnedItems() = viewModelScope.launch {
        val itemsToCommit = _sessionEarnedItems.value
        if (itemsToCommit.isEmpty()) return@launch

        itemsToCommit.forEach { (itemId, count) ->
            repo.updateInventory(itemId, count)
        }
        _sessionEarnedItems.value = emptyMap()
        loadHealthPotionQuantity()
        loadAllItems()
    }

    fun clearSessionItems() {
        _sessionEarnedItems.value = emptyMap()
    }

    fun updateInventory(itemId: Long, quantity: Int) = viewModelScope.launch {
        repo.updateInventory(itemId, quantity)

        if (itemId == 1L) {
            loadHealthPotionQuantity()
        }
        loadAllItems()
    }

    private fun loadAllItems() = viewModelScope.launch {
        _allItems.value = repo.getAllItems()
    }

}