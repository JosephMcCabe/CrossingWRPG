package com.example.crossingwrpg.com.example.crossingwrpg.data

import androidx.room.TypeConverter
import com.example.crossingwrpg.EarnedItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ItemListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromItemList(list: List<EarnedItem>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toItemList(json: String?): List<EarnedItem>? {
        if (json == null) return emptyList()
        val type = object : TypeToken<List<EarnedItem>>() {}.type
        return gson.fromJson(json, type)
    }
}