package com.example.crossingwrpg.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

object PopulateItems {
    private fun insertItem(
        db: SupportSQLiteDatabase,
        name: String,
        description: String,
        slotDb: String
    ) {
        db.execSQL(
            "INSERT INTO item (name, description, slot) VALUES (?, ?, ?)",
            arrayOf(name, description, slotDb)
        )
    }
    fun callback() = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.beginTransaction()
            try {
                insertItem(
                    db,
                    name = "Red Potion",
                    description = "Restores a small amount of health.",
                    slotDb = "consumable"
                )
                insertItem(
                    db,
                    name = "Iron Sword",
                    description = "A sharp iron blade.",
                    slotDb = "weapon"
                )
                insertItem(
                    db,
                    name = "Purple Potion",
                    description = "Like a red potion! but purple.",
                    slotDb = "consumable"
                )
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
}