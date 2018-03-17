package blue.sparse.minecraft.common

import blue.sparse.minecraft.common.util.ProxyProvider

object Minecraft {

	lateinit var side: Side
		private set

	private val proxyProvider by lazy { ProxyProvider<MinecraftProxy>("blue.sparse.minecraft.client.MinecraftClient", "blue.sparse.minecraft.server.MinecraftServer") }

	val proxy: MinecraftProxy
		get() = proxyProvider.value

	fun init(side: Side) {
		if(this::side.isInitialized)
			throw IllegalStateException("Already initialized")

		this.side = side
	}

	enum class Side {
		CLIENT, SERVER
	}
}