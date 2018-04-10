package blue.sparse.minecraft.common.world

import blue.sparse.extensions.nextDirection
import blue.sparse.math.abs
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.biome.BiomeType
import blue.sparse.minecraft.common.block.*
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.impl.types.EntityTypeItem
import blue.sparse.minecraft.common.entity.impl.types.stack
import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.util.math.BlockFace
import blue.sparse.minecraft.common.util.random
import blue.sparse.minecraft.common.world.chunk.Chunk

data class BlockView(val chunk: Chunk, val xInChunk: Int, val yInChunk: Int, val zInChunk: Int) {

	val x: Int = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.x, chunk.regionChunkPosition.x, xInChunk)
	val y: Int = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.y, chunk.regionChunkPosition.y, yInChunk)
	val z: Int = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.z, chunk.regionChunkPosition.z, zInChunk)

	val world = chunk.world

	val position: Vector3i
		get() = Vector3i(x, y, z)

	var block: Block
		get() = chunk.getBlock(xInChunk, yInChunk, zInChunk)
		set(value) = chunk.setBlock(xInChunk, yInChunk, zInChunk, value)

	var type: BlockType?
		get() = block.type
		set(value) {
			block = Block(value)
		}

	var state: BlockState
		get() = block.state
		set(value) {
			block = Block(type, value)
		}

	var biome: BiomeType
		get() = chunk.getBiome(xInChunk, yInChunk, zInChunk)
		set(value) = chunk.setBiome(xInChunk, yInChunk, zInChunk, value)

	val blockLight: Vector3i
		get() = chunk.getBlockLight(xInChunk, yInChunk, zInChunk)

	val blockLightFloat: Vector3f
		get() = chunk.getBlockLightFloat(xInChunk, yInChunk, zInChunk)

	val sunLight: Int
		get() = chunk.getSunLight(xInChunk, yInChunk, zInChunk)

	val sunLightFloat: Float
		get() = chunk.getSunLightFloat(xInChunk, yInChunk, zInChunk)

	fun relative(x: Int, y: Int, z: Int): BlockView {
		return chunk.world.getOrGenerateBlock(this.x + x, this.y + y, this.z + z)
	}

	fun relative(face: BlockFace): BlockView {
		return relative(face.x, face.y, face.z)
	}

	fun asItem(): Item<*>? {
		return Item(type?.item ?: return null)
	}

	fun dropItemNaturally(): Boolean {
		val item = asItem() ?: return false

		type = null

		val itemEntity = Entity(EntityTypeItem, world, position.toFloatVector() + 0.5f)
		itemEntity.stack = item.stack()
		itemEntity.velocity = random.nextDirection().apply { y = abs(y) } * 2f
		itemEntity.add()

		return true
	}
}