package blue.sparse.minecraft.common.block

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.block.impl.*
import blue.sparse.minecraft.common.item.impl.types.ItemTypeBlock
import blue.sparse.minecraft.common.util.*

abstract class BlockType(val identifier: Identifier, val hasItem: Boolean = true) {
	internal val id: Int

	open val proxy: BlockTypeProxy by ProxyProvider<BlockTypeProxy>(
			"blue.sparse.minecraft.client.block.proxy.Default",
			"blue.sparse.minecraft.server.block.proxy.Default",
			this
	)

	val item = if(hasItem) ItemTypeBlock(this) else null

	open val boundingBox = AABB(Vector3f(0f), Vector3f(1f))

	open val transparent: Boolean = false

	constructor(id: String) : this(Identifier(id))

	init {
		id = registry.size + 1
		register(this)
	}

	abstract class BlockTypeProxy(val blockType: BlockType) : Proxy

	companion object {
		private val registry = LinkedHashMap<Identifier, BlockType>()
		private val idRegistry = LinkedHashMap<Int, BlockType>()

		private fun register(type: BlockType) {
			if (type.identifier in registry)
				throw IllegalArgumentException("Block with identifier \"${type.identifier}\" is already registered.")

			registry[type.identifier] = type
			idRegistry[type.id] = type
		}

		operator fun get(identifier: Identifier) = registry[identifier]

		operator fun get(name: String) = get(Identifier(name))

		internal operator fun get(id: Int) = idRegistry[id]

		val stone = BlockStone
		val dirt = BlockDirt
		val cobblestone = BlockCobblestone
		val sand = BlockSand
	}
}