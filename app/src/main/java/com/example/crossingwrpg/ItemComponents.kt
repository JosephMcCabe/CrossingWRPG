package com.example.crossingwrpg

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crossingwrpg.data.EquipmentSlot
import com.example.crossingwrpg.data.Item

@Composable
fun ItemWithCount(
    name: String,
    itemID: Int,
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PixelText(
            name,
            fontSize = 20.sp
        )
        Spacer(Modifier.height(4.dp))

        Box(modifier = Modifier.wrapContentSize()) {
            Image(
                painter = painterResource(itemID),
                contentDescription = name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp)
            )

            if (count > 1) {
                PixelText(
                    "x$count",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = 0.dp)
                        .background(Color.White, CircleShape)
                        .border(BorderStroke(1.dp, Color.Black), CircleShape)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun EarnedItemsDisplay(
    earnedItems: Map<Long, Int>,
    allItems: List<Item>,
    onWeaponClick: ((Item) -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PixelText(
            "Items added to inventory:",
            fontSize = 30.sp
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            earnedItems.forEach { (itemId, count) ->
                val droppableItem = allItems.find { it.itemId == itemId }
                if (droppableItem != null) {
                    Column(
                        modifier = Modifier.weight(1f, fill = false),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ItemWithCount(
                            name = droppableItem.name,
                            itemID = droppableItem.drawableId,
                            count = count
                        )

                        if (onWeaponClick != null && droppableItem.slot == EquipmentSlot.WEAPON) {
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { onWeaponClick(droppableItem) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.height(40.dp)
                            ) {
                                PixelText(
                                    "Equip",
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}