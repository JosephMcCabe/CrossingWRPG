package com.example.crossingwrpg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

data class InventoryRow(
    val itemId: Long,
    val name: String,
    val description: String,
    val slot: EquipmentSlot,
    val quantity: Int
)

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(row: Inventory): Long

    @Query("UPDATE inventory" +
            " SET quantity = quantity + :by" +
            " WHERE userId = :userId AND itemId = :itemId")
    suspend fun updateQuantity(userId: Int, itemId: Long, by: Int): Int

    @Query("SELECT quantity" +
            " FROM inventory" +
            " WHERE userId = :userId AND itemId = :itemId")
    suspend fun getQuantity(userId: Int, itemId: Long): Int?

    @Query("SELECT *" +
            " FROM inventory" +
            " WHERE userId = :userId AND itemId = :itemId")
    suspend fun getItemById(userId: Int, itemId: Long): Inventory?

    @Query("SELECT *" +
            " FROM inventory" +
            " WHERE userId = :userId" +
            " ORDER BY itemId")
    suspend fun getAll(userId: Int): List<Inventory>

    @Query("SELECT *" +
            " FROM item")
    suspend fun getAllItems(): List<Item>

    @Query("SELECT item.itemId, item.name, item.description, item.slot, inventory.quantity" +
            " FROM inventory" +
            " INNER JOIN item ON item.itemId = inventory.itemId" +
            " WHERE inventory.userId = :userId AND item.itemId = :itemId")
    suspend fun getInventoryWithDetails(userId: Int, itemId: Long): InventoryRow?

    @Query("SELECT *" +
            " FROM item" +
            " WHERE itemId = :itemId")
    suspend fun getItemById(itemId: Long): Item?

    @Query("DELETE " +
            " FROM inventory" +
            " WHERE userId = :userId" +
            " AND itemId = :itemId")
    suspend fun delete(userId: Int, itemId: Long): Int

}