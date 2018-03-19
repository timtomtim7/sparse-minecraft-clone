package blue.sparse.minecraft.client.world.proxy

import blue.sparse.minecraft.client.world.render.OfflineChunkModel
import blue.sparse.minecraft.common.world.Chunk

class ClientChunkProxy(chunk: Chunk): Chunk.ChunkProxy(chunk) {

	fun generateOfflineModel(): OfflineChunkModel {
		return OfflineChunkModel(chunk)
	}

}