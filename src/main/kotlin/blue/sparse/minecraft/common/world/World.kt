package blue.sparse.minecraft.common.world

class World {
	fun getChunk(x: Int, y: Int, z: Int): Chunk {
		TODO()
	}

	fun getBlock(x: Int, y: Int, z: Int): BlockView {
		TODO()
	}

	operator fun get(x: Int, y: Int, z: Int): BlockView {
		return getBlock(x, y, z)
	}
}