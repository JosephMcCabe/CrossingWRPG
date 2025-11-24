package com.example.crossingwrpg.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User (
    @PrimaryKey
    val uid: Int = 1,
    val name: String,
    val totalSteps: Long = 0,
    val totalWalkingSeconds: Int = 0,
    val totalItems: Int = 0,
    val vitality: Int = 100,
    val strength: Int = 25,
    val speed: Int = 1,
    val mind: Int = 1,
    val enemiesDefeated: Int = 0
)