package com.example.crossingwrpg

data class EarnedItem(
    val id: String,
    val name: String,
    val drawableId: Int,
    val count: Int,
    val dropThreshold: Int
)

val ALL_DROPPABLE_ITEMS = listOf(
    EarnedItem(id = "red_potion", name = "Red Potion", drawableId = R.drawable.pixelpotion, count = 1, dropThreshold = 10),
    EarnedItem(id = "potion_red", name = "Purple Potion", drawableId = R.drawable.purplepotion, count = 1, dropThreshold = 15),
    EarnedItem(id = "sword_iron", name = "Sword", drawableId = R.drawable.pixelsword, count = 1, dropThreshold = 20)
)