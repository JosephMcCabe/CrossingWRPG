package com.example.crossingwrpg.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User (
    @PrimaryKey val uid: Int = 1,
    val name: String,
    val totalSteps: Int = 0,
    val totalWalkingSeconds: Int = 0

)