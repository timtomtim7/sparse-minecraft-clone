package blue.sparse.minecraft.common.inventory.impl.types

import blue.sparse.minecraft.common.inventory.impl.Section
import blue.sparse.minecraft.common.inventory.impl.SlotType

abstract class InventoryType {
	protected val _sections = ArrayList<Section.Definition>()

	val slotCount: Int
		get() = sections.sumBy(Section.Definition::size)

	val sections: List<Section.Definition>
		get() = _sections

	protected fun addSection(key: Section.Key, size: Int = 1, slotType: SlotType = SlotType.Default, input: Boolean = false, output: Boolean = false) {
		_sections.add(Section.Definition(key, slotType, size, input, output))
	}

	protected fun addPrimarySection(key: Section.Key, size: Int = 1, slotType: SlotType = SlotType.Default) {
		addSection(key, size, slotType, true, true)
	}

}