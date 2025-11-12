package com.example.crossingwrpg.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class InventoryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy {
        val db = AppDatabase.getDatabase(app)
        InventoryRepository(db.inventoryDao())
    }
}