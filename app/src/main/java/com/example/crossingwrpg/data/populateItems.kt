package com.example.crossingwrpg.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.crossingwrpg.R

object PopulateItems {
    private fun insertItem(
        db: SupportSQLiteDatabase,
        itemId: Long,
        name: String,
        description: String,
        slotDb: String,
        drawableId: Int,
        dropThreshold: Int
    ) {
        db.execSQL(
            "INSERT INTO item (itemId, name, description, slot, drawableId, dropThreshold) VALUES (?, ?, ?, ?, ?, ?)",
            arrayOf(itemId, name, description, slotDb, drawableId, dropThreshold)
        )
    }
    fun callback() = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.beginTransaction()
            try {
                insertItem(
                    db,
                    itemId = 1L,
                    name = "Health Potion",
                    description = "Restores a small amount of health.",
                    slotDb = "consumable",
                    drawableId = R.drawable.pixelpotion,
                    dropThreshold = 10
                )
                insertItem(
                    db,
                    itemId = 2L,
                    name = "Iron Sword",
                    description = "A sharp iron blade.",
                    slotDb = "weapon",
                    drawableId = R.drawable.pixelsword,
                    dropThreshold = 15
                )
                insertItem(
                    db,
                    itemId = 3L,
                    name = "Purple Potion",
                    description = "Like a health potion! but purple.",
                    slotDb = "consumable",
                    drawableId = R.drawable.purplepotion,
                    dropThreshold = 20
                )
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
}