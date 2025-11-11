package com.example.crossingwrpg.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import android.content.Context
import androidx.room.TypeConverters

@Database(
    entities = [User::class, Item::class, Equipped::class, Inventory::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(EquipmentSlotConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao
    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "crossing_database")
                    .addCallback(PopulateItems.callback())
                    .fallbackToDestructiveMigration()
                    .build().also { Instance = it }
            }
        }
    }
}