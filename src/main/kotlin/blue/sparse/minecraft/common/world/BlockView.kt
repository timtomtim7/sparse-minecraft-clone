package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.biome.BiomeType
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.util.math.BlockFace

class BlockView(val chunk: Chunk, val xInChunk: Int, val yInChunk: Int, val zInChunk: Int) {

	val x: Int = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.x, chunk.regionChunkPosition.x, xInChunk)
	val y: Int = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.y, chunk.regionChunkPosition.y, yInChunk)
	val z: Int = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.z, chunk.regionChunkPosition.z, zInChunk)

	val position: Vector3i
		get() = Vector3i(x, y, z)

	internal var raw: Int
		get() = chunk.getRaw(xInChunk, yInChunk, zInChunk)
		set(value) = chunk.setRaw(xInChunk, yInChunk, zInChunk, value)

	//TODO: There is probably a more simple way to do these things

	internal var typeID: Int
		get() = Chunk.typeID(raw)
		set(value) { raw = Chunk.typeID(raw, value) }

	internal var stateID: Int
		get() = Chunk.stateID(raw)
		set(value) { raw = Chunk.stateID(raw, value) }

	var type: BlockType?
		get() = Chunk.type(raw)
		set(value) { raw = Chunk.type(raw, value) }

	var blockLight: Int
		get() = Chunk.blockLight(raw)
		set(value) { raw = Chunk.blockLight(raw, value) }

	var skyLight: Int
		get() = Chunk.skyLight(raw)
		set(value) { raw = Chunk.skyLight(raw, value) }

	internal var biomeID: Int
		get() = Chunk.biomeID(raw)
		set(value) { raw = Chunk.biomeID(raw, value) }

	var biome: BiomeType
		get() = Chunk.biome(raw)
		set(value) { raw = Chunk.biome(raw, value) }

	fun relative(x: Int, y: Int, z: Int): BlockView {
		return chunk.world.getOrGenerateBlock(this.x + x, this.y + y, this.z + z)
	}

	fun relative(face: BlockFace): BlockView {
		return relative(face.x, face.y, face.z)
	}
}