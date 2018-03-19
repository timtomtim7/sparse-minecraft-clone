package blue.sparse.minecraft.client

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.asset.AssetManager
import blue.sparse.engine.asset.provider.AssetArchive
import blue.sparse.engine.window.Window
import blue.sparse.minecraft.common.Minecraft
import java.io.File

fun main(args: Array<String>) {
	Minecraft.init(Minecraft.Side.CLIENT)

	val window = Window(
			args.getOrNull(0)?.toIntOrNull() ?: 1600,
			args.getOrNull(1)?.toIntOrNull() ?: 900
	) {
		resizable()
		icon("sparse_icon_64.png")
		vSync(true)
	}

	AssetManager.registerProvider(AssetArchive(File("faithful.zip"), "assets/"))
//	AssetManager.registerProvider(AssetArchive(File("PureBDcraft  64x MC112.zip"), "assets/"))
	SparseEngine.start(window, MinecraftClient::class, 0.0)
}