package blue.sparse.minecraft.common.inventory

import blue.sparse.minecraft.common.item.ItemStack
import blue.sparse.minecraft.common.item.ItemType

object TestInventory : Inventory(5) {
    init {
        addItem(ItemStack(ItemType.acaciaBoat, 5))
        addItem(ItemStack(ItemType.acaciaBoat, 15))
        println("INVENTORY: ${content.contentToString()}")

    }
}