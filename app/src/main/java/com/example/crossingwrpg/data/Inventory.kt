package com.example.crossingwrpg.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "inventory",
    primaryKeys = ["userId", "itemId"],
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
        Index("itemId"),
    ]
)
data class Inventory (
    val userId: Int = 1,
    val itemId: Long,
    val quantity: Int
)
