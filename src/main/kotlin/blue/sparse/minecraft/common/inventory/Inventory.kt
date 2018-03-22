package blue.sparse.minecraft.common.inventory

import blue.sparse.minecraft.common.inventory.impl.Section
import blue.sparse.minecraft.common.inventory.impl.types.InventoryType
import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.item.ItemStack

class Inventory(val type: InventoryType) : Iterable<ItemStack<*>?> {

	val size = type.slotCount

	private val _sections = LinkedHashMap<Section.Key, Section>()
	private val _inputSections = ArrayList<Section>()
	private val _outputSections = ArrayList<Section>()

	val sections: List<Section>
		get() = _sections.values.toList()

	val outputSections: List<Section>
		get() = _outputSections

	val inputSections: List<Section>
		get() = _inputSections

	init {
		for (secDef in type.sections) {
			val section = Section(secDef)
			_sections[secDef.key] = section
			if (secDef.input) _inputSections.add(section)
			if (secDef.output) _outputSections.add(section)
		}
	}

	operator fun contains(key: Section.Key): Boolean {
		return _sections.containsKey(key)
	}

	operator fun get(key: Section.Key): Section {
		return _sections[key] ?: throw IllegalArgumentException("Inventory does not contain section with key $key")
	}

	operator fun contains(stack: ItemStack<*>): Boolean {
		return _sections.values.any { stack in it }
	}

	fun containsInInput(stack: ItemStack<*>): Boolean {
		return _inputSections.any { stack in it }
	}

	fun containsInOutput(stack: ItemStack<*>): Boolean {
		return _outputSections.any { stack in it }
	}

	fun addStack(stack: ItemStack<*>, inputOnly: Boolean = true): Int {
		val sections = if (inputOnly) _inputSections else _sections.values

		val clonedStack = stack.deepCopy()

		for (section in sections) {
			if (clonedStack.amount <= 0)
				break

			clonedStack.amount = section.addStack(clonedStack)
		}

		return clonedStack.amount
	}

	fun addItem(item: Item<*>, amount: Int = 1, inputOnly: Boolean = true): Int {
		return addStack(item.stack(amount), inputOnly)
	}

	fun removeStack(stack: ItemStack<*>, outputOnly: Boolean = true): Int {
		val sections = if (outputOnly) _outputSections else _sections.values

		val clonedStack = stack.deepCopy()

		for (section in sections) {
			if (clonedStack.amount <= 0)
				break

			clonedStack.amount = section.removeStack(clonedStack)
		}

		return clonedStack.amount
	}

	fun removeItem(item: Item<*>, amount: Int = 1, outputOnly: Boolean = true): Int {
		return removeStack(item.stack(amount), outputOnly)
	}

	operator fun plusAssign(stack: ItemStack<*>) {
		addStack(stack)
	}

	operator fun plusAssign(item: Item<*>) {
		addItem(item)
	}

	operator fun minusAssign(stack: ItemStack<*>) {
		removeStack(stack)
	}

	operator fun minusAssign(item: Item<*>) {
		removeItem(item)
	}

	fun clear() {
		_sections.values.forEach(Section::clear)
	}

	override fun iterator(): Iterator<ItemStack<*>?> {
		return _sections.values.fold<Section, Sequence<ItemStack<*>?>?>(null) { i, v ->
			v.asSequence() + (i ?: emptySequence())
		}!!.iterator()
	}
}