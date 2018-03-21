package blue.sparse.minecraft.common

import blue.sparse.minecraft.common.util.ProxyProvider
import blue.sparse.minecraft.common.world.World

object Minecraft {

	lateinit var side: Side
		private set

	private val proxyProvider by lazy { ProxyProvider<MinecraftProxy>(
			"blue.sparse.minecraft.client.MinecraftClient",
			"blue.sparse.minecraft.server.MinecraftServer"
	) }

	val proxy: MinecraftProxy
		get() = proxyProvider.value

	lateinit var world: World

	fun init(side: Side) {
		if(this::side.isInitialized)
			throw IllegalStateException("Already initialized")

		this.side = side

		regenerateWorld()
	}

	fun regenerateWorld() {
		val world = World("overworld")
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