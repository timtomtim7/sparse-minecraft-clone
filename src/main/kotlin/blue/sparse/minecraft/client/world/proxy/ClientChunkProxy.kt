package blue.sparse.minecraft.client.world.proxy

import blue.sparse.engine.render.resource.model.BasicModel
import blue.sparse.minecraft.client.world.render.OfflineChunkModel
import blue.sparse.minecraft.common.world.chunk.Chunk
import java.util.concurrent.ConcurrentLinkedQueue

class ClientChunkProxy(chunk: Chunk): Chunk.ChunkProxy(chunk) {
	private var offline: OfflineChunkModel? = null
	private var modelLastGenerated: Long = 0L
	private var chunkLastModified: Long = 0L

	var model: BasicModel? = null
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
		get() = (offline == null && model == null) || modelLastGenerated < chunkLastModified

	override fun changed(x: Int, y: Int, z: Int) {
		chunkLastModified = System.currentTimeMillis()

		if (x == 0) relativeChanged(-1, 0, 0)
		if (y == 0) relativeChanged(0, -1, 0)
		if (z == 0) relativeChanged(0, 0, -1)
		if (x == Chunk.MASK) relativeChanged(1, 0, 0)
		if (y == Chunk.MASK) relativeChanged(0, 1, 0)
		if (z == Chunk.MASK) relativeChanged(0, 0, 1)
	}

	fun generateOfflineModel() {
		offline = OfflineChunkModel(chunk)
		modelLastGenerated = System.currentTimeMillis()
	}

	fun uploadOfflineModel(): Boolean {
		val offline = offline ?: return false
		this.offline = null

		deleteModel()
		model = offline.upload()
		return true
	}

	override fun unloaded() {
		deleteQueue.add(this)
	}

	private fun relativeChanged(x: Int, y: Int, z: Int) {
		val pos = chunk.worldChunkPosition
		val relative = chunk.world.getChunk(pos.x + x, pos.y + y, pos.z + z) ?: return

		(relative.proxy as ClientChunkProxy).chunkLastModified = System.currentTimeMillis()
	}

	private fun deleteModel() {
		model?.array?.delete()
//		model?.delete()
	}

	companion object {
		private val deleteQueue = ConcurrentLinkedQueue<ClientChunkProxy>()

		fun update() {
			for(i in 1..16)
				deleteQueue.poll()?.deleteModel()
//			deleteQueue.removeAll {
//				it.deleteModel()
//				true
//			}
		}
	}
}