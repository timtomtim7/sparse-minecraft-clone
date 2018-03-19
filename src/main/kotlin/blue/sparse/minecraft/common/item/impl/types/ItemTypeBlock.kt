package blue.sparse.minecraft.common.item.impl.types

import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.ProxyProvider

open class ItemTypeBlock(val blockType: BlockType): ItemType(blockType.identifier) {

	override val proxy: ItemTypeProxy by ProxyProvider<ItemTypeProxy>(
			"blue.sparse.minecraft.client.item.proxy.ItemTypeBlockProxy",
			"blue.sparse.minecraft.server.item.proxy.Default",
			this
	)

}