package blue.sparse.minecraft.client.world.proxy

import blue.sparse.engine.render.resource.model.VertexArray
import blue.sparse.minecraft.client.world.render.OfflineChunkModel
import blue.sparse.minecraft.common.world.Chunk

class ClientChunkProxy(chunk: Chunk): Chunk.ChunkProxy(chunk) {

	private var offline: OfflineChunkModel? = null
	private var modelLastGenerated: Long = 0L

	var model: VertexArray? = null
		get() {
			offline?.let {
				field?.delete()
				field = it.upload()
				offline = null
			}
			return field
		}
		private set

	val canGenerateModel: Boolean
		get() {
			val wc = chunk.worldChunkPosition
			val world = chunk.world

			return  world.getChunk(wc.x + 1, wc.y, wc.z) != null &&
					world.getChunk(wc.x - 1, wc.y, wc.z) != null &&
					world.getChunk(wc.x, wc.y + 1, wc.z) != null &&
					world.getChunk(wc.x, wc.y - 1, wc.z) != null &&
					world.getChunk(wc.x, wc.y, wc.z + 1) != null &&
					world.getChunk(wc.x, wc.y, wc.z - 1) != null
		}

	val shouldGenerateModel: Boolean
		get() = modelLastGenerated < chunk.lastChangedMillis

	fun generateOfflineModel() {
		offline = OfflineChunkModel(chunk)
		modelLastGenerated = System.currentTimeMillis()
	}
}