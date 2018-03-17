package blue.sparse.minecraft.client.item.proxy

import blue.sparse.engine.asset.Asset
import blue.sparse.minecraft.common.item.ItemType

class Default(itemType: ItemType) : ClientItemTypeProxy(itemType) {
	override val texture = Asset["${itemType.identifier.namespace}/textures/items/${itemType.identifier.name}.png"]
}