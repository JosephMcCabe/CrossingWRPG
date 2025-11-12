package com.example.crossingwrpg.data

class InventoryRepository(private val dao: InventoryDao) {
    val userId = 1
    val consume = -1
    suspend fun updateInventory(itemId: Long, addQuantity: Int) {
        require(addQuantity > 0)
        val item = dao.updateQuantity(userId, itemId, addQuantity)
        if (item == 0) {
            dao.insert(Inventory(userId, itemId, addQuantity))
        }
    }

    suspend fun consumeItem(itemId: Long) {
        val item = dao.updateQuantity(userId, itemId, consume)
        if (item == 0) return

        val quantity = dao.getQuantity(userId, itemId) ?: 0
        if (quantity <= 0) {
            dao.delete(userId, itemId)
        }
    }

    suspend fun getQuantity(itemId: Long): Int {
        return dao.getQuantity(userId, itemId) ?: 0
    }

    suspend fun getItemWithDetails(itemId: Long): InventoryRow? {
        return dao.getInventoryWithDetails(userId, itemId)
    }

    suspend fun getAll(): List<Inventory>{
        return dao.getAll(userId)
    }
}