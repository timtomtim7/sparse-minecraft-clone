package blue.sparse.minecraft.client

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.SparseGame
import blue.sparse.engine.window.input.Key
import blue.sparse.engine.window.input.MouseButton
import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.floats.floor
import blue.sparse.minecraft.client.gui.GUIManager
import blue.sparse.minecraft.client.gui.TestGUI
import blue.sparse.minecraft.client.util.Debug
import blue.sparse.minecraft.client.world.proxy.ClientWorldProxy
import blue.sparse.minecraft.client.world.render.WorldRenderer
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.MinecraftProxy
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.ProxyHolder
import java.io.File
import javax.imageio.ImageIO

class MinecraftClient : SparseGame(), MinecraftProxy {

	var time = 0f
		private set

	init {
		camera.apply {
			move(Vector3f(0f, -30f, 10f))
			controller = MinecraftController(this, movementSpeed = 7f)
		}
	}

	private fun resetCameraProjection() {
		camera.projection = Matrix4f.perspective(100f, window.aspectRatio, 0.1f, 1000f)
	}

	override fun postInit() {
		ItemType
		BlockType
		EntityType

		GUIManager.open(TestGUI)
	}

	override fun update(delta: Float) {
		super.update(delta)
		time += delta

		if (window.resized)
			resetCameraProjection()

		if (input[Key.F9].pressed) {
			ImageIO.write(WorldRenderer.atlas.texture.read(), "png", File("block_item_atlas.png"))
			ImageIO.write(GUIManager.atlas.texture.read(), "png", File("gui_atlas.png"))
		}

		if (input[Key.F8].pressed) {
			Minecraft.regenerateWorld()
		}

		if (input[MouseButton.RIGHT].pressed || input[MouseButton.RIGHT].heldTime >= 1f) {
			val pos = floor(camera.transform.translation + camera.transform.rotation.forward * 5f).toIntVector()
			Minecraft.world.getOrGenerateBlock(pos.x, pos.y, pos.z).type = BlockType.diamondBlock
		}

		if (input[MouseButton.LEFT].pressed || input[MouseButton.LEFT].heldTime >= 1f) {
			val pos = floor(camera.transform.translation + camera.transform.rotation.forward * 5f).toIntVector()
			Minecraft.world.getBlock(pos.x, pos.y, pos.z)?.type = null
		}

		Minecraft.world.update(delta)
		GUIManager.update(delta)
	}

	override fun render(delta: Float) {
		(Minecraft.world.proxy as ClientWorldProxy).renderer.render(delta)

		Debug.renderTemp()
//		(camera.controller as MinecraftController).bounds.debugRender(camera.transform.translation, Vector3f(0f, 0.333f, 1f))

		GUIManager.render(delta)
	}

	companion object : ProxyHolder<MinecraftClient> {

		override val proxy: MinecraftClient
			get() = SparseEngine.game as MinecraftClient

	}
}