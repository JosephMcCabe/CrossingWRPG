package com.example.crossingwrpg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(row: Inventory)

    @Query("UPDATE inventory" +
            " SET quantity = quantity + :by" +
            " WHERE userId = :userId" +
            " AND itemId = :itemId")
    suspend fun updateQuantity(userId: Int, itemId: Long, by: Int): Int

    @Query("SELECT quantity" +
            " FROM inventory" +
            " WHERE userId = :userId" +
            " AND itemId = :itemId")
    suspend fun getQuantity(userId: Int, itemId: Long): Int?

    suspend fun delete(userId: Int, itemId: Long)

    suspend fun addOrRemove(UserId: Int, itemId: Long, by:Int) {

    }
}