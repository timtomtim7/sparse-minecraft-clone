package blue.sparse.minecraft.common.block

import blue.sparse.minecraft.common.block.impl.*
import blue.sparse.minecraft.common.item.impl.ItemTypeBlock
import blue.sparse.minecraft.common.util.*

abstract class BlockType(val identifier: Identifier, val hasItem: Boolean = true) {
	open val proxy: BlockTypeProxy by ProxyProvider<BlockTypeProxy>(
			"blue.sparse.minecraft.client.block.proxy.Default",
			"blue.sparse.minecraft.server.block.proxy.Default",
			this
	)

	val item = if(hasItem) ItemTypeBlock(this) else null

	constructor(id: String) : this(Identifier(id))

	init {
		register(this)
	}

	abstract class BlockTypeProxy(val blockType: BlockType) : Proxy

	companion object {
		internal val registry = LinkedHashMap<Identifier, BlockType>()

		private fun register(type: BlockType) {
			if (type.identifier in registry)
				throw IllegalArgumentException("Block with identifier \"${type.identifier}\" is already registered.")

			registry[type.identifier] = type
		}

		val dirt = BlockDirt
		val stone = BlockStone
		val cobblestone = BlockCobblestone
	}
}