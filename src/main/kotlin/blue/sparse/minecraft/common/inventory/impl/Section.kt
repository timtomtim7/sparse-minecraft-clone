package blue.sparse.minecraft.common.inventory.impl

import blue.sparse.minecraft.common.item.ItemStack
import kotlin.math.max
import kotlin.math.min

class Section(val definition: Definition): Iterable<ItemStack<*>?> {

	private val content = Array<ItemStack<*>?>(definition.size) { null }

	val full: Boolean
		get() = content.all { it != null && it.amount >= min(it.item.type.maxStackSize, definition.slotType.maxStackSize) }

	val empty: Boolean
		get() = content.all { it == null }

	val firstEmptySlot: Int
		get() = content.indexOf(null)

	init {
		if (!definition.slotType.accepts(null))
			throw IllegalArgumentException("Sections must be able to accept null")
	}

	operator fun get(index: Int): ItemStack<*>? {
		return content[index]
	}

	operator fun set(index: Int, value: ItemStack<*>?): Boolean {
		if(!definition.slotType.accepts(value))
			return false

		content[index] = value
		return true
	}

	override fun iterator(): Iterator<ItemStack<*>?> {
		return content.iterator()
	}

	fun clear() {
		content.fill(null)
	}

	fun addStack(stack: ItemStack<*>): Int {
		if(!definition.slotType.accepts(stack))
			return stack.amount

		if (stack.amount <= 0)
			return stack.amount

		val maxStackSize = min(stack.item.type.maxStackSize, definition.slotType.maxStackSize)

		var remaining = stack.amount

		for(i in content.indices) {
			if(remaining <= 0)
				return 0

			val contained = content[i]

			// If the slot is empty, add as many items as possible to that slot
			if(contained == null) {
				val amount = max(min(remaining, maxStackSize), remaining % maxStackSize)

				content[i] = stack.deepCopy(amount)
				remaining -= amount

				continue
			}

			// If the slot is not empty but the items don't match, skip it
			if(contained.item != stack.item)
				continue

			// If the slot is already full, skip it
			if(contained.amount >= maxStackSize)
				continue

			// The amount of items that can be added to this slot before it is full
			val untilFull = maxStackSize - contained.amount

			// If there are more remaining than it would take to fill up the slot then fill up the slot
			if(remaining >= untilFull) {
				contained.amount = maxStackSize
				remaining -= untilFull

				continue
			}

			// In this case, the only possibility is that there are less than would overfill the slot, so add all and it's done.
			contained.amount += remaining
			return 0
		}

		return remaining
	}

	fun removeStack(stack: ItemStack<*>): Int {
		if (stack.amount <= 0)
			return stack.amount

		var remaining = stack.amount

		// Loop through the contents in reverse
		for(i in content.indices.reversed()) {
			if(remaining <= 0)
				return 0

			// Can't remove from empty slots, so immediately skip it
			val contained = content[i] ?: continue

			// Only want to remove items if they match
			if(contained.item != stack.item)
				continue

			// Attempting to remove more than available in this slot,
			if(remaining >= contained.amount) {
				remaining -= contained.amount
				content[i] = null
				continue
			}

			// In this case, the only possibility is that there are less to remove than in the slot, so remove the rest and it's done.
			contained.amount -= remaining
			return 0
		}

		return remaining
	}

	fun contains(stack: ItemStack<*>, allowSplitStack: Boolean): Boolean {
		if (stack.amount <= 0)
			return true

		if(!allowSplitStack)
			return content.any { it != null && it.item == stack.item && it.amount >= stack.amount }

		var remaining = stack.amount

		// Loop through the contents
		for(i in content.indices) {
			if(remaining <= 0)
				return true

			// Empty slots don't contain items
			val contained = content[i] ?: continue

			// Only want to count items if they match
			if(contained.item != stack.item)
				continue

			if(remaining >= contained.amount) {
				remaining -= contained.amount
				continue
			}

			return true
		}

		return remaining <= 0
	}

	operator fun contains(stack: ItemStack<*>): Boolean {
		return contains(stack, true)
	}

	class Definition(
			val key: Key,
			val slotType: SlotType,
			val size: Int,
			val input: Boolean,
			val output: Boolean
	) {
		val name = key.name
	}

	abstract class Key(val name: String) {
		object Storage: 		Key("storage")

		object OffHand: 		Key("offhand")
		object MainHand: 		Key("mainhand")

		object Hotbar: 			Key("hotbar")

		object Helmet: 			Key("helmet")
		object Chestplate: 		Key("chestplate")
		object Leggings: 		Key("leggings")
		object Boots: 			Key("boots")
	}
}