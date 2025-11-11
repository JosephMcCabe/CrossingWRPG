package com.example.crossingwrpg.data

import androidx.room.TypeConverter
enum class EquipmentSlot(val db: String) {
    HEAD("head"),
    CHEST("chest"),
    ARMS("arms"),
    LEGS("legs"),
    BOOTS("boots"),
    WEAPON("weapon"),
    CONSUMABLE("consumable")
}

class EquipmentSlotConverters {
    @TypeConverter
    fun toDb(slot: EquipmentSlot?): String? = slot?.db

    @TypeConverter
    fun fromDb(value: String?): EquipmentSlot? =
        EquipmentSlot.entries.firstOrNull { it.db == value }
}