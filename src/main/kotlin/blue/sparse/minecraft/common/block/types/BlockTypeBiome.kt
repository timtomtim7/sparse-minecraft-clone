package blue.sparse.minecraft.common.block.types

import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.util.Identifier
import blue.sparse.minecraft.common.util.proxy.ProxyProvider

abstract class BlockTypeBiome(identifier: Identifier) : BlockType(identifier) {
	constructor(id: String) : this(Identifier(id))

	override val proxy: BlockTypeProxy by ProxyProvider<BlockTypeProxy>(
			"blue.sparse.minecraft.client.block.proxy.BlockTypeBiomeProxy",
			"blue.sparse.minecraft.server.block.proxy.Default",
			this
	)

}