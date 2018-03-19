package blue.sparse.minecraft.common.world

import blue.sparse.minecraft.common.block.BlockType

class BlockView(val chunk: Chunk, val xInChunk: Int, val yInChunk: Int, val zInChunk: Int) {

	private var data: Int
		get() = chunk.getRaw(xInChunk, yInChunk, zInChunk)
		set(value) = chunk.setRaw(xInChunk, yInChunk, zInChunk, value)

	internal var typeID: Int
		get() = (data shr 0) and 0xFFF
		set(value) {
			data = (data.inv() or 0xFFF).inv() or value
		}

	internal var stateID: Int
		get() = (data shr 12) and 0xF
		set(value) {
			data = (data.inv() or (0xF shl 12)).inv() or (value shl 12)
		}

	var type: BlockType?
		get() = typeID.takeIf { it > 0 }?.let { BlockType[it] }
		set(value) {
			typeID = value?.id ?: 0
		}

	var light: Int
		get() = (data shr 16) and 0xF
		set(value) {
			data = (data.inv() or (0xF shl 16)).inv() or (value shl 16)
		}

	var skyLight: Int
		get() = (data shr 20) and 0xF
		set(value) {
			data = (data.inv() or (0xF shl 20)).inv() or (value shl 20)
		}

	internal var biomeID: Int
		get() = (data shr 24) and 0xFF
		set(value) {
			data = (data.inv() or (0xFF shl 24)).inv() or (value shl 24)
		}

}