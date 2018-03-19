package blue.sparse.minecraft.client.world.proxy

import blue.sparse.engine.render.resource.model.BasicModel
import blue.sparse.engine.render.resource.model.Model
import blue.sparse.minecraft.client.world.render.OfflineChunkModel
import blue.sparse.minecraft.common.world.Chunk

class ClientChunkProxy(chunk: Chunk): Chunk.ChunkProxy(chunk) {

	private var offline: OfflineChunkModel? = null
	private var modelLastGenerated: Long = 0L

	var model: Model? = null
		get() {
			offline?.let {
				field = BasicModel(it.upload())
				offline = null
			}
			return field
		}
		private set

	fun generateOfflineModel() {
		offline = OfflineChunkModel(chunk)
		modelLastGenerated = System.currentTimeMillis()
	}

}