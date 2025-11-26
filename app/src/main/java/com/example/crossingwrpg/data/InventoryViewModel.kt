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
    private val _sessionEarnedItems = MutableStateFlow<Map<Long, Int>>(emptyMap())
    private val _allItems = MutableStateFlow<List<Item>>(emptyList())
    private val _inventoryWithQuantities = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val healthPotionQuantity: StateFlow<Int> = _healthPotionQuantity.asStateFlow()
    val sessionEarnedItem: StateFlow<Map<Long, Int>> = _sessionEarnedItems.asStateFlow()
    val allItems: StateFlow<List<Item>> = _allItems.asStateFlow()
    val inventoryWithQuantities: StateFlow<Map<Long, Int>> = _inventoryWithQuantities.asStateFlow()

    init {
        loadHealthPotionQuantity()
        loadAllItems()
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
        loadInventoryQuantities()
    }
    fun useHealthPotion() = viewModelScope.launch {
        repo.consumeItem(1L)
        loadHealthPotionQuantity()
        loadInventoryQuantities()
    }

    fun loadHealthPotionQuantity() = viewModelScope.launch {
        _healthPotionQuantity.value = repo.getQuantity(1L)
    }
    fun loadInventoryQuantities() = viewModelScope.launch {
        val allItemsList = repo.getAllItems()
        val quantities = mutableMapOf<Long, Int>()

        allItemsList.forEach { item ->
            val quantity = repo.getQuantity(item.itemId)
            if (quantity > 0) {
                quantities[item.itemId] = quantity
            }
        }
        _inventoryWithQuantities.value = quantities
    }
    private fun loadAllItems() = viewModelScope.launch {
        _allItems.value = repo.getAllItems()
        loadInventoryQuantities()
    }

}