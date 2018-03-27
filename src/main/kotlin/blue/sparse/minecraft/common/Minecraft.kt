package blue.sparse.minecraft.common

import blue.sparse.minecraft.common.util.ProxyProvider
import blue.sparse.minecraft.common.world.World
import blue.sparse.minecraft.common.world.generator.TestChunkGenerator

object Minecraft {

	lateinit var side: Side
		private set

	private val proxyProvider by lazy {
		ProxyProvider<MinecraftProxy>(
				"blue.sparse.minecraft.client.MinecraftClient",
				"blue.sparse.minecraft.server.MinecraftServer"
		)
	}

	val proxy: MinecraftProxy
		get() = proxyProvider.value

	lateinit var world: World

	private lateinit var thread: TickingThread

	val partialTicks: Float
		get() = thread.partialTicks.toFloat()

	fun init(side: Side) {
		if (this::side.isInitialized)
			throw IllegalStateException("Already initialized")

		this.side = side

		thread = TickingThread("CommonTickingThread", this::onTick)
		thread.isDaemon = true
		thread.start()
		regenerateWorld()
	}

	fun onTick(delta: Float) {
		if(this::world.isInitialized)
			world.update(delta)
	}

	fun regenerateWorld() {
		println("REGENERATING WORLD")
		val world = World("overworld", generator = TestChunkGenerator)
		Minecraft.world = world

//		for (x in -64..64) {
//			for (z in -64..64) {
//				val maxY = (STBPerlin.stb_perlin_noise3(x * 0.05f, 0f, z * 0.05f, 1024, 1024, 1024) * 8 + 16).toInt()
//
//				for (y in 0..maxY) {
//					world.getOrGenerateBlock(x, y, z).type = BlockType.dirt
//				}
//			}
//		}
	}

	enum class Side {
		CLIENT, SERVER
	}
}