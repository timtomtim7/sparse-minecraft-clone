package blue.sparse.minecraft.server.world.proxy

import blue.sparse.minecraft.common.world.Chunk

class ServerChunkProxy(chunk: Chunk) : Chunk.ChunkProxy(chunk) {
	override fun changed(x: Int, y: Int, z: Int, before: Int, new: Int) {

	}

	override fun unloaded() {

	}
}