package com.example.crossingwrpg.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import android.content.Context
import androidx.room.TypeConverters
import com.example.crossingwrpg.com.example.crossingwrpg.data.ItemListConverter

@Database(entities = [User::class], version = 1, exportSchema = false)
@TypeConverters(ItemListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "crossing_database")
                    .build().also { Instance = it }
            }
        }
    }
}