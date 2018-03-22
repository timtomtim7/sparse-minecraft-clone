package blue.sparse.minecraft.common.inventory.impl.types

import blue.sparse.minecraft.common.inventory.impl.Section
import blue.sparse.minecraft.common.inventory.impl.SlotType
import blue.sparse.minecraft.common.item.impl.types.armor.*

object InventoryTypeEntityEquipment : InventoryType() {

	init {
		addPrimarySection(Section.Key.Helmet, 1, SlotType.Typed<ItemTypeHelmet>())
		addPrimarySection(Section.Key.Chestplate, 1, SlotType.Typed<ItemTypeChestplate>())
		addPrimarySection(Section.Key.Leggings, 1, SlotType.Typed<ItemTypeLeggings>())
		addPrimarySection(Section.Key.Boots, 1, SlotType.Typed<ItemTypeBoots>())

		addPrimarySection(Section.Key.MainHand)
		addSection(Section.Key.OffHand)
	}

}