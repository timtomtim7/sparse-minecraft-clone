package blue.sparse.minecraft.common.inventory

import blue.sparse.minecraft.common.item.ItemStack

abstract class Inventory(val size: Int, val content: Array<ItemStack<*>?> = Array(size) { null }) {

    val full get() = content.filterNotNull().size > size
    val empty get() = content.all { it == null }
    val firstEmptySlot get() = content.indexOf(content.find { it == null })

    //  TODO: Come back after rendering is finished and fix data copyng bug
    fun addItem(stack: ItemStack<*>) {
        if (full || stack.amount <= 0)
            return

        val sameStacks = content.filter { it?.item == stack.item && it.item.type.maxStackSize > it.amount }.filterNotNull()

        if (sameStacks.isEmpty() && !full) {

            val stackAmount = stack.amount / stack.item.type.maxStackSize
            val extraAmount = stack.amount % stack.item.type.maxStackSize

            for (x in 0 until stackAmount) {
                if (firstEmptySlot >= 0)
                    content[firstEmptySlot] = ItemStack(stack.item, stack.item.type.maxStackSize)

            }

            if (firstEmptySlot >= 0)
                content[firstEmptySlot] = ItemStack(stack.item, extraAmount)

            return
        }

        var leftOver = stack.amount

        for (sameStack in sameStacks) {
            if (leftOver == 0) return
            val amtUntilFull = sameStack.item.type.maxStackSize - sameStack.amount
            if (leftOver >= amtUntilFull) {
                sameStack.amount += leftOver - amtUntilFull
                leftOver -= amtUntilFull
            }
        }

        if (leftOver > 0 && !full)
            content[firstEmptySlot] = stack
    }

    fun removeItem(stack: ItemStack<*>) {
        if (empty || stack.amount <= 0)
            return

        val sameStacks = content.filter { it?.item == stack.item && it.amount != 0 }.filterNotNull().reversed()

        var leftToRemove = stack.amount

        for (sameStack in sameStacks) {
            if (leftToRemove == 0) return

            if (leftToRemove >= sameStack.amount) {
                leftToRemove -= sameStack.amount
                val index = content.lastIndexOf(sameStack)
                if (index >= 0) {
                    content[index] = null
                    continue
                }
            }
            sameStack.amount -= leftToRemove
            break
        }
    }

    operator fun get(index: Int) = content[index]
    operator fun set(index: Int, item: ItemStack<*>) {
        content[index] = item
    }

}