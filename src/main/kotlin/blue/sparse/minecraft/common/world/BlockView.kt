package blue.sparse.minecraft.common.world

class BlockView(val chunk: Chunk, val xInChunk: Int, val yInChunk: Int, val zInChunk: Int) {

	private val data: Int
		get() = chunk.getRaw(xInChunk, yInChunk, zInChunk)

	val typeID: Int
		get() = (data shr 0) and 0xFFF

	val stateID: Int
		get() = (data shr 12) and 0xF

	val light: Int
		get() = (data shr 16) and 0xF

	val skyLight: Int
		get() = (data shr 20) and 0xF

	val biomeID: Int
		get() = (data shr 24) and 0xFF

}