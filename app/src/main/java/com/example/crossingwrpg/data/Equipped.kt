package com.example.crossingwrpg.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(
    tableName = "equipped",
    primaryKeys = ["userId", "slot"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("userId"),
        Index("slot"),
        Index("itemId")
    ]
)

data class Equipped (
    val userId: Int = 1,
    val slot: EquipmentSlot,
    val itemId: Long
)