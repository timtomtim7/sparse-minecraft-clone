package blue.sparse.minecraft.client

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.SparseGame
import blue.sparse.engine.window.Window
import blue.sparse.engine.window.input.Key
import blue.sparse.engine.window.input.MouseButton
import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.client.gui.GUIManager
import blue.sparse.minecraft.client.gui.TestGUI
import blue.sparse.minecraft.client.player.ClientPlayer
import blue.sparse.minecraft.client.util.Debug
import blue.sparse.minecraft.client.world.proxy.ClientWorldProxy
import blue.sparse.minecraft.client.world.render.WorldRenderer
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.MinecraftProxy
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.entity.impl.types.living.EntityTypePlayer
import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.ProxyHolder
import java.io.File
import javax.imageio.ImageIO

class MinecraftClient : SparseGame(), MinecraftProxy {

	var time = 0f
		private set

	var viewEntity: Entity<*>? = null
		set(value) {
			camera.controller = value?.let { EntityCameraController(camera, value) }
			field = value
		}

	init {
		camera.apply {
			move(Vector3f(0f, -30f, 10f))
//			controller = MinecraftController(this, movementSpeed = 7f)
			viewEntity?.let {
				controller = EntityCameraController(this, it)
			}
		}
	}

	private fun resetCameraProjection() {
		camera.projection = Matrix4f.perspective(100f, window.aspectRatio, 0.1f, 1000f)
	}

	override fun postInit() {
		ItemType
		BlockType
		EntityType

		ClientPlayer.entity = Minecraft.world.spawnEntity(EntityTypePlayer, Vector3f(0f, 128f, 0f))
		viewEntity = ClientPlayer.entity

		val world = Minecraft.world
		val renderDistance = (world.proxy as ClientWorldProxy).renderer.renderDistance
		for(c in renderDistance) {
			world.getOrGenerateBlock(c.x, c.y, c.z)
		}

		GUIManager.open(TestGUI)
	}

	override fun update(delta: Float) {
		super.update(delta)
		time += delta

		handleMouseCapture()
		ClientPlayer.input(window.input, delta)

		if (window.resized)
			resetCameraProjection()

		if (input[Key.F9].pressed) {
			ImageIO.write(WorldRenderer.atlas.texture.read(), "png", File("block_item_atlas.png"))
			ImageIO.write(GUIManager.atlas.texture.read(), "png", File("gui_atlas.png"))
		}

		if (input[Key.F8].pressed) {
			Minecraft.regenerateWorld()
		}

		val targetBlock = viewEntity?.getTargetBlock(32f)
		if(targetBlock != null) {
			val breakPos = targetBlock.block.position
			val placePos = breakPos + targetBlock.face.offset

			Debug.addTempCube(breakPos.toFloatVector(), breakPos.toFloatVector() + 1f, Vector3f(1f, 0f, 0f))
			Debug.addTempCube(placePos.toFloatVector(), placePos.toFloatVector() + 1f)

			if (input[MouseButton.RIGHT].pressed || input[MouseButton.RIGHT].heldTime >= 1f) {
				Minecraft.world.getOrGenerateBlock(placePos.x, placePos.y, placePos.z).type = BlockType.diamondBlock
			}

			if (input[MouseButton.LEFT].pressed || input[MouseButton.LEFT].heldTime >= 1f) {
				Minecraft.world.getBlock(breakPos.x, breakPos.y, breakPos.z)?.type = null
			}
		}


		Minecraft.world.update(delta)
		GUIManager.update(delta)
	}

	override fun render(delta: Float) {
		(Minecraft.world.proxy as ClientWorldProxy).renderer.render(delta)

		Debug.renderTemp()

		GUIManager.render(delta)
	}

	companion object : ProxyHolder<MinecraftClient> {

		override val proxy: MinecraftClient
			get() = SparseEngine.game as MinecraftClient

	}

	private fun handleMouseCapture() {
		val window = SparseEngine.window
		val input = window.input

		if (window.cursorMode == Window.CursorMode.NORMAL && (input[Key.ESCAPE].pressed || input[MouseButton.LEFT].pressed))
			window.cursorMode = Window.CursorMode.DISABLED

		if (window.cursorMode == Window.CursorMode.DISABLED && (input[Key.ESCAPE].pressed))
			window.cursorMode = Window.CursorMode.NORMAL
	}
}