package com.example.crossingwrpg.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.crossingwrpg.EarnedItem
import com.example.crossingwrpg.com.example.crossingwrpg.data.ItemListConverter

@Entity(tableName = "user")
@TypeConverters(ItemListConverter::class)
data class User (
    @PrimaryKey val uid: Int = 1,
    val name: String,
    val totalSteps: Long = 0,
    val totalWalkingSeconds: Int = 0,
    val totalItems: Int = 0,
    val sessionItems: List<EarnedItem>? = emptyList(),
    val redPotions: Int = 0,
    val purplePotions: Int = 0,
    val sword: Int = 0
)