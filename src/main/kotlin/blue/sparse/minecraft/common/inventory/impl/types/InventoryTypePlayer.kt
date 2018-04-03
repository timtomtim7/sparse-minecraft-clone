package blue.sparse.minecraft.common.inventory.impl.types

import blue.sparse.minecraft.common.inventory.impl.Section.Key.*
import blue.sparse.minecraft.common.inventory.impl.SlotType
import blue.sparse.minecraft.common.item.types.armor.*

object InventoryTypePlayer : InventoryType() {

	init {
		addPrimarySection(Hotbar, 9)
		addPrimarySection(Storage, 9 * 3)

		addSection(OffHand)

		addSection(Helmet, 1, SlotType.Typed<ItemTypeHelmet>())
		addSection(Chestplate, 1, SlotType.Typed<ItemTypeChestplate>())
		addSection(Leggings, 1, SlotType.Typed<ItemTypeLeggings>())
		addSection(Boots, 1, SlotType.Typed<ItemTypeBoots>())
	}

}