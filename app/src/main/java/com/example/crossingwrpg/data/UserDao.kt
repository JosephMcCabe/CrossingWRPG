package com.example.crossingwrpg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE uid = :id")
    fun observeById(id: Int): Flow<User?>

    @Query("SELECT * FROM user Where uid = :id")
    suspend fun getById(id: Int): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: User)

    @Query("UPDATE user SET name = :name WHERE uid = 1")
    suspend fun updateName(name: String)

    @Query("UPDATE user SET totalSteps = totalSteps + :addSteps WHERE uid = 1")
    suspend fun addToTotalSteps(addSteps: Int)
}