package com.example.crossingwrpg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.crossingwrpg.EarnedItem

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

    @Query("UPDATE user SET name = :name WHERE uid = 1")
    suspend fun updateName(name: String)

    @Query("UPDATE user SET totalSteps = totalSteps + :addSteps WHERE uid = 1")
    suspend fun addToTotalSteps(addSteps: Int)

    @Query("UPDATE user SET totalWalkingSeconds = totalWalkingSeconds + :addSeconds WHERE uid = 1")
    suspend fun addToTotalTime(addSeconds: Int)

    @Query("UPDATE user SET totalItems = totalItems + :itemsCount WHERE uid = 1")
    suspend fun addToTotalItems(itemsCount: Int)

    @Query("UPDATE user SET sessionItems = :items WHERE uid = 1")
    suspend fun updateSessionItems(items: List<EarnedItem>)

    @Query("UPDATE user SET redPotions = redPotions + :count WHERE uid = 1")
    suspend fun addRedPotions(count:Int)

    @Query("UPDATE user SET redPotions = redPotions - 1 WHERE uid = 1 AND redPotions > 0")
    suspend fun consumeRedPotion()

    @Query("UPDATE user SET purplePotions = purplePotions + :count WHERE uid = 1")
    suspend fun addPurplePotions(count:Int)

    @Query("UPDATE user SET purplePotions = purplePotions - 1 WHERE uid = 1 AND purplePotions > 0")
    suspend fun consumePurplePotion()

    @Query("UPDATE user SET redPotions = sword + :count WHERE uid = 1")
    suspend fun sword(count:Int)

    @Query("UPDATE user SET sword = sword + :count WHERE uid = 1")
    suspend fun addSword(count: Int)
}