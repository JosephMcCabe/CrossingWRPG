package com.example.crossingwrpg.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class Item (
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0L,
    val name: String,
    val description: String,
    val slot: EquipmentSlot,
    val drawableId: Int,
    val dropThreshold: Int
)