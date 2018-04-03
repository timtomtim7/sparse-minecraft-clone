package blue.sparse.minecraft.common.world.generator.thread

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.util.getValue
import blue.sparse.minecraft.common.util.weak
import blue.sparse.minecraft.common.world.World

class ChunkGenerationThread(world: World, val provider: Sequence<Vector3i>) : Thread("ChunkGenerationThread") {

	val world by weak(world)

	init {
		isDaemon = true
	}

	override fun run() {
		while (true) {
			val world = world ?: break
			val chunk = provider.firstOrNull() ?: continue

//			println("Generating $chunk")
			world.getOrGenerateChunk(chunk)

			yield()
		}
	}
}