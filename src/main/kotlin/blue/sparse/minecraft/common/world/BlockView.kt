package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.block.BlockType

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
		get() = (raw shr 0) and 0xFFF
		set(value) {
			raw = (raw.inv() or 0xFFF).inv() or value
		}

	internal var stateID: Int
		get() = (raw shr 12) and 0xF
		set(value) {
			raw = (raw.inv() or (0xF shl 12)).inv() or (value shl 12)
		}

	var type: BlockType?
		get() = typeID.takeIf { it > 0 }?.let { BlockType[it] }
		set(value) {
			typeID = value?.id ?: 0
		}

	var light: Int
		get() = (raw shr 16) and 0xF
		set(value) {
			raw = (raw.inv() or (0xF shl 16)).inv() or (value shl 16)
		}

	var skyLight: Int
		get() = (raw shr 20) and 0xF
		set(value) {
			raw = (raw.inv() or (0xF shl 20)).inv() or (value shl 20)
		}

	internal var biomeID: Int
		get() = (raw shr 24) and 0xFF
		set(value) {
			raw = (raw.inv() or (0xFF shl 24)).inv() or (value shl 24)
		}

}