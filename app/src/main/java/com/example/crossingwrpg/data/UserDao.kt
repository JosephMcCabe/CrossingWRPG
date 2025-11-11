package com.example.crossingwrpg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE uid = :id")
    fun observeById(id: Int): Flow<User?>

    @Query("SELECT * FROM user WHERE uid = 1")
    suspend fun getCurrentUser(): User?

    @Query("SELECT * FROM user Where uid = :id")
    suspend fun getById(id: Int): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE user SET redPotions = redPotions - 1 WHERE uid = 1 AND redPotions > 0")
    suspend fun consumeRedPotion()

    @Query("UPDATE user SET purplePotions = purplePotions - 1 WHERE uid = 1 AND purplePotions > 0")
    suspend fun consumePurplePotion()
}