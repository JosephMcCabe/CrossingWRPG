package com.example.crossingwrpg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user WHERE uid = :id")
    fun observeByID(id: Int): Flow<User?>

    @Query("SELECT * FROM user Where uid = :id")
    suspend fun getById(id: Int): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(stats: User)

    @Query("UPDATE user SET name = :name WHERE uid = :id")
    suspend fun updateName(id: Int = 1, name: String)

    @Query("UPDATE user SET totalSteps = :totalSteps WHERE uid = :id")
    suspend fun updateSteps(id: Int = 1, totalSteps: Int)
}