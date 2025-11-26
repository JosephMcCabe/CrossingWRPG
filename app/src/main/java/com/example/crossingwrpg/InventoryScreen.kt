package com.example.crossingwrpg

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crossingwrpg.data.EquipmentSlot
import com.example.crossingwrpg.data.InventoryViewModel
import com.example.crossingwrpg.data.Item

@Composable
fun InventoryScreen(
    inventoryVm: InventoryViewModel,
    onDismiss: () -> Unit
) {
    val allItems by inventoryVm.allItems.collectAsState(initial = emptyList())
    val inventoryQuantities by inventoryVm.inventoryWithQuantities.collectAsState(initial = emptyMap())
    var selectedWeapon by remember { mutableStateOf<Item?>(null) }

    LaunchedEffect(Unit) {
        inventoryVm.loadInventoryQuantities()
    }

    val itemsByCategory = getItemsByCategory(allItems, inventoryQuantities)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { CenteredDialogTitle("INVENTORY") },
        text = { InventoryContent(itemsByCategory) { selectedWeapon = it } },
        confirmButton = {
            DialogButton(
                text = "CLOSE",
                onClick = onDismiss
            )
        }
    )

    selectedWeapon?.let { weapon ->
        WeaponDetailsDialog(
            weapon = weapon,
            onDismiss = { selectedWeapon = null },
            onEquip = {
                // TODO: Implement weapon equip logic
                selectedWeapon = null
            }
        )
    }
}

@Composable
private fun WeaponDetailsDialog(
    weapon: Item,
    onDismiss: () -> Unit,
    onEquip: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { CenteredDialogTitle(weapon.name, fontSize = 30.sp) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(weapon.drawableId),
                    contentDescription = weapon.name,
                    modifier = Modifier.size(96.dp)
                )
                Spacer(Modifier.height(16.dp))
                PixelText(weapon.description, fontSize = 20.sp)
            }
        },
        confirmButton = {
            DialogButton(
                text = "EQUIP",
                onClick = onEquip
            )
        },
        dismissButton = {
            DialogButton(
                text = "CANCEL",
                onClick = onDismiss
            )
        }
    )
}

@Composable
private fun InventoryContent(
    itemsByCategory: Map<EquipmentSlot, List<ItemData>>,
    onWeaponClick: (Item) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ItemCategorySection(
            title = "Items",
            items = itemsByCategory[EquipmentSlot.CONSUMABLE],
            onItemClick = null
        )

        ItemCategorySection(
            title = "Equipment",
            items = itemsByCategory[EquipmentSlot.WEAPON],
            onItemClick = onWeaponClick
        )
    }
}

@Composable
private fun ItemCategorySection(
    title: String,
    items: List<ItemData>?,
    onItemClick: ((Item) -> Unit)?
) {
    if (items.isNullOrEmpty()) return

    PixelText(
        title,
        fontSize = 28.sp
    )
    Spacer(Modifier.height(8.dp))

    items.forEach { itemData ->
        InventoryItemRow(
            itemData = itemData,
            onItemClick = onItemClick
        )
    }

    Spacer(Modifier.height(16.dp))
}

@Composable
private fun InventoryItemRow(
    itemData: ItemData,
    onItemClick: ((Item) -> Unit)?
) {
    val isClickable = onItemClick != null && itemData.item.slot == EquipmentSlot.WEAPON

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .then(if (isClickable) Modifier.clickable { onItemClick.invoke(itemData.item) } else Modifier),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemWithCount(
            name = itemData.item.name,
            itemID = itemData.item.drawableId,
            count = itemData.quantity
        )
    }
}

@Composable
private fun CenteredDialogTitle(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 35.sp
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        PixelText(
            text,
            fontSize = fontSize
        )
    }
}

private data class ItemData(
    val item: Item,
    val quantity: Int,
    val slot: EquipmentSlot
)

private fun getItemsByCategory(
    allItems: List<Item>,
    inventoryQuantities: Map<Long, Int>
): Map<EquipmentSlot, List<ItemData>> {
    return inventoryQuantities.mapNotNull { (itemId, quantity) ->
        allItems.find { it.itemId == itemId }?.let { item ->
            ItemData(item, quantity, item.slot)
        }
    }.groupBy { it.slot }
}