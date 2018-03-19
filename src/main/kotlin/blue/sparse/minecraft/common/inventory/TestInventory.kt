package blue.sparse.minecraft.common.inventory

import blue.sparse.minecraft.common.item.ItemStack
import blue.sparse.minecraft.common.item.ItemType

object TestInventory : Inventory(5) {
    init {
        println("current inventory: ${content.contentToString()}")
        addItem(ItemStack(ItemType.acaciaBoat, 5))
        println("added 5 acaciaBoats")
        println("current inventory: ${content.contentToString()}")
        addItem(ItemStack(ItemType.apple, 5000))
        println("added 5000 apples")
        println("current inventory: ${content.contentToString()}")
        removeItem(ItemStack(ItemType.apple, 32))
        println("removed 32 apples")
        println("current inventory: ${content.contentToString()}")
    }
}