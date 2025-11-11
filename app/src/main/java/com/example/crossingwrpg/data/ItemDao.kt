package com.example.crossingwrpg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<Item>): List<Long>

    @Query("SELECT COUNT(*) FROM item")
    suspend fun count(): Int
}