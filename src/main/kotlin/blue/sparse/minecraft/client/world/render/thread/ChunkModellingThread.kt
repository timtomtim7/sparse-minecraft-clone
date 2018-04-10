package blue.sparse.minecraft.client.world.render.thread

import blue.sparse.minecraft.client.world.proxy.ClientChunkProxy
import blue.sparse.minecraft.common.util.getValue
import blue.sparse.minecraft.common.util.weak
import blue.sparse.minecraft.common.world.World
import blue.sparse.minecraft.common.world.chunk.Chunk

class ChunkModellingThread(world: World, val provider: Sequence<Chunk>) : Thread("ChunkModellingThread") {

	val world by weak(world)

	init {
		isDaemon = true
	}

	override fun run() {
		while (world != null) {
			val chunk = provider.firstOrNull() ?: continue

			(chunk.proxy as ClientChunkProxy).generateOfflineModel()

			yield()
		}
	}
}