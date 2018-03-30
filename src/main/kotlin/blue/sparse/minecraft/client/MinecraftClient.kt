package blue.sparse.minecraft.client

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.SparseGame
import blue.sparse.engine.asset.Asset
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.window.Window
import blue.sparse.engine.window.input.Key
import blue.sparse.engine.window.input.MouseButton
import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.floats.Quaternion4f
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.client.block.proxy.ClientBlockTypeProxy
import blue.sparse.minecraft.client.entity.render.*
import blue.sparse.minecraft.client.gui.GUIManager
import blue.sparse.minecraft.client.gui.TestGUI
import blue.sparse.minecraft.client.item.proxy.ClientItemTypeProxy
import blue.sparse.minecraft.client.player.ClientPlayer
import blue.sparse.minecraft.client.util.Debug
import blue.sparse.minecraft.client.world.proxy.ClientWorldProxy
import blue.sparse.minecraft.client.world.render.WorldRenderer
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.MinecraftProxy
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.proxy.ProxyHolder
import org.lwjgl.opengl.GL11
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

	private val entityModel: EntityModel = EntityModel.load(Asset["minecraft/models/entities/test.json"])
	private lateinit var entityPose: Pose
	private lateinit var animation: Animation
	private var animationTime: Float = 0f

	init {
		setupAnimation()
	}

	private fun resetCameraProjection() {
		camera.projection = Matrix4f.perspective(100f, window.aspectRatio, 0.1f, 1000f)
	}

	override fun postInit() {
		ItemType.registry.values.forEach { (it.proxy as ClientItemTypeProxy).sprite }
		BlockType.registry.values.forEach { (it.proxy as ClientBlockTypeProxy).apply {
			backSprite
			bottomSprite
			frontSprite
			leftSprite
			rightSprite
			topSprite
		} }
		EntityType

		ClientPlayer.addEntity(Minecraft.world)
		viewEntity = ClientPlayer.entity
		Minecraft.addPlayer(ClientPlayer)

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

		if(input[Key.F7].pressed) {
			setupAnimation()
		}

		GUIManager.update(delta)
	}

	override fun render(delta: Float) {
		engine.clear()
		val wireframeButton = input[Key.F]
		if (wireframeButton.pressed) {
			glCall { GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE) }
			glCall { GL11.glDisable(GL11.GL_CULL_FACE) }
		} else if (wireframeButton.released) {
			glCall { GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL) }
			glCall { GL11.glEnable(GL11.GL_CULL_FACE) }
		}

		(Minecraft.world.proxy as ClientWorldProxy).renderer.render(delta)

		animationTime += delta
		animation.applyToPose(entityPose, animationTime)
		entityModel.render(entityPose, camera.viewProjectionMatrix, Matrix4f.translation(Vector3f(0f, 40f, 0f)))

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

		if (window.cursorMode == Window.CursorMode.NORMAL && (input[Key.ESCAPE].pressed || input[MouseButton.LEFT].pressed)) {
			window.cursorMode = Window.CursorMode.DISABLED
			ClientPlayer.resetMousePosition(input)
		}

		if (window.cursorMode == Window.CursorMode.DISABLED && (input[Key.ESCAPE].pressed))
			window.cursorMode = Window.CursorMode.NORMAL
	}

	private fun setupAnimation() {
		entityPose = entityModel.createPose()

		val a = entityModel.createPose()
		val legAmount = 1f
		val armAmount = 1f

		a["left_leg"].setRotation(Quaternion4f(Vector3f(1f, 0f, 0f), legAmount))
		a["right_arm"].setRotation(Quaternion4f(Vector3f(1f, 0f, 0f), armAmount))
		a["right_leg"].setRotation(Quaternion4f(Vector3f(1f, 0f, 0f), -legAmount))
		a["left_arm"].setRotation(Quaternion4f(Vector3f(1f, 0f, 0f), -armAmount))

		val b = entityModel.createPose()
		b["left_leg"].setRotation(Quaternion4f(Vector3f(1f, 0f, 0f), -legAmount))
		b["right_arm"].setRotation(Quaternion4f(Vector3f(1f, 0f, 0f), -armAmount))
		b["right_leg"].setRotation(Quaternion4f(Vector3f(1f, 0f, 0f), legAmount))
		b["left_arm"].setRotation(Quaternion4f(Vector3f(1f, 0f, 0f), armAmount))

		animation = Animation(listOf(
				Animation.KeyFrame(a, 0f),
				Animation.KeyFrame(b, 0.3f),
				Animation.KeyFrame(a, 0.6f)
		))
	}
}