package blue.sparse.minecraft.common

import blue.sparse.minecraft.common.player.Player
import blue.sparse.minecraft.common.util.ProxyProvider
import blue.sparse.minecraft.common.world.World
import blue.sparse.minecraft.common.world.generator.TestChunkGenerator
import java.util.concurrent.ConcurrentHashMap

object Minecraft {

	lateinit var side: Side
		private set

	private lateinit var thread: TickingThread

	// <proxy>
	private val proxyProvider by lazy {
		ProxyProvider<MinecraftProxy>(
				"blue.sparse.minecraft.client.MinecraftClient",
				"blue.sparse.minecraft.server.MinecraftServer"
		)
	}

	val proxy: MinecraftProxy
		get() = proxyProvider.value
	// </proxy>

	lateinit var world: World
		private set

	// <ticks>
	val tickRate: Double
		get() = thread.tickRate

	var partialTicks: Float = 0f
		get() = thread.partialTicks.toFloat()
	// </ticks>

	// <players>
	private val _players = ConcurrentHashMap.newKeySet<Player>()

	val players: Set<Player>
		get() = _players
	// </player>

	fun init(side: Side) {
		if (this::side.isInitialized)
			throw IllegalStateException("Already initialized")

		this.side = side

		thread = TickingThread("CommonTickingThread", 20.0, this::onTick)
		thread.isDaemon = true
		thread.start()
		regenerateWorld()
	}

	fun regenerateWorld() {
		Minecraft.world = World("world", generator = TestChunkGenerator)
	}

	fun addPlayer(player: Player): Boolean {
		return _players.add(player)
	}

	private fun onTick(delta: Float) {
		if(this::world.isInitialized)
			world.update(delta)
	}

	enum class Side {
		CLIENT, SERVER

	}
}
