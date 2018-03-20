package blue.sparse.minecraft.common.inventory

import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.item.ItemStack
import kotlin.math.min

abstract class Inventory(val size: Int, val content: Array<ItemStack<*>?> = Array(size) { null }) {

	val full get() = content.filterNotNull().size > size
	val empty get() = content.all { it == null }
	val firstEmptySlot get() = content.indexOf(content.find { it == null })

	fun addItem(item: Item<*>, amount: Int = 1) = addItem(ItemStack(item, amount))

	fun addItem(stack: ItemStack<*>) {
		if (full || stack.amount <= 0)
			return

		val sameStacks = content.filter { it?.item == stack.item && it.item.type.maxStackSize > it.amount }.filterNotNull()

		if (sameStacks.isEmpty() && !full) {
			val stackAmount = stack.amount / stack.item.type.maxStackSize
			val extraAmount = stack.amount % stack.item.type.maxStackSize

			for (i in 0 until stackAmount) {
				if (firstEmptySlot >= 0)
					content[firstEmptySlot] = stack.deepCopy(stack.item.type.maxStackSize)
			}

			if (firstEmptySlot >= 0 && extraAmount > 0)
				content[firstEmptySlot] = stack.deepCopy(extraAmount)
			return
		}

		var leftOver = stack.amount

		for (sameStack in sameStacks) {
			if (leftOver == 0) return

			val amtUntilFull = sameStack.item.type.maxStackSize - sameStack.amount
			if (amtUntilFull == 0) continue

			val amt = min(leftOver, amtUntilFull)
			sameStack.amount += amt
			leftOver -= amt
		}

		if (leftOver > 0 && !full)
			content[firstEmptySlot] = stack.deepCopy(leftOver)
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

	fun clear() {
		content.fill(null)
	}

	operator fun get(index: Int) = content[index]
	operator fun set(index: Int, value: ItemStack<*>?) {
		content[index] = value
	}
}