package blue.sparse.minecraft.client.item.proxy

import blue.sparse.minecraft.client.block.proxy.ClientBlockTypeProxy
import blue.sparse.minecraft.common.item.impl.ItemTypeBlock

class ItemTypeBlockProxy(itemType: ItemTypeBlock) : ClientItemTypeProxy(itemType) {
//	override val texture = Asset["minecraft/textures/blocks/${itemType.identifier.name}.png"]

	override val texture = (itemType.blockType.proxy as ClientBlockTypeProxy).frontTexture

	override val model by lazy {
		(itemType.blockType.proxy as ClientBlockTypeProxy).generateItemModel()!!
	}
}