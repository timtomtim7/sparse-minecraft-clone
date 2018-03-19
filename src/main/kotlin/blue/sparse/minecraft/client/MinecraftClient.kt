package blue.sparse.minecraft.client

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.SparseGame
import blue.sparse.engine.asset.Asset
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.render.camera.FirstPerson
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.model.Model
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.engine.window.input.Key
import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.ints.Vector2i
import blue.sparse.minecraft.client.gui.GUIManager
import blue.sparse.minecraft.client.gui.TestGUI
import blue.sparse.minecraft.client.item.ItemComponent
import blue.sparse.minecraft.client.util.BlankShader
import blue.sparse.minecraft.client.world.proxy.ClientChunkProxy
import blue.sparse.minecraft.client.world.proxy.ClientWorldProxy
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.MinecraftProxy
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.ProxyHolder
import org.lwjgl.opengl.GL11
import java.io.File
import javax.imageio.ImageIO

class MinecraftClient : SparseGame(), MinecraftProxy {

	val atlas = TextureAtlas(Vector2i(1024, 512))

//	val sky = OverworldSky()

	var time = 0f
		private set

	val chunkShader = ShaderProgram(
			Asset["minecraft/shaders/blocks.fs"],
			Asset["minecraft/shaders/blocks.vs"]
	)

	lateinit var chunkModel: Model

	init {
		camera.apply {
			move(Vector3f(0f, 0f, 10f))
			controller = FirstPerson(this, movementSpeed = 10.92f)
		}
	}

	fun spawnItem(item: Item<*>, position: Vector3f) {
		scene.add(ItemComponent(item, position))
	}

	private fun resetCameraProjection() {
		camera.projection = Matrix4f.perspective(100f, window.aspectRatio, 0.1f, 1000f)
	}

	override fun postInit() {
		ItemType
		BlockType

		val item = Item(ItemType.ironChestplate)
		item.enchantColor = 0x00FF00
		item.editNBT { list("ench", emptyList()) }
		spawnItem(item, Vector3f(0f))

		GUIManager.open(TestGUI)

		val world = Minecraft.world

		world.getOrGenerateBlock(5, 5, 5).type = BlockType.stone
		world.getOrGenerateBlock(5, 6, 5).type = BlockType.cobblestone
		world.getOrGenerateBlock(5, 7, 5).type = BlockType.cobblestone
		world.getOrGenerateBlock(6, 7, 5).type = BlockType.cobblestone
		world.getOrGenerateBlock(7, 7, 5).type = BlockType.cobblestone
		world.getOrGenerateBlock(7, 7, 6).type = BlockType.cobblestone
		world.getOrGenerateBlock(7, 7, 7).type = BlockType.dirt

		val chunk = world.getChunk(0, 0, 0)!!
		val chunkProxy = chunk.proxy as ClientChunkProxy
		chunkProxy.generateOfflineModel()
		chunkModel = chunkProxy.model!!

//		val itemTypes = ItemType.registry.values
//
//		val squareSize = Math.ceil(Math.sqrt(itemTypes.size.toDouble())).toInt()
//
//		for((i, itemType) in itemTypes.withIndex()) {
//			val x = (i % squareSize) - (squareSize / 2)
//			val y = (i / squareSize) - (squareSize / 2)
//			val item = Item(itemType)
//
//			spawnItem(item, Vector3f(x.toFloat(), y.toFloat(), 0f) * 0.8f)
//		}

//		ImageIO.write(atlas.texture.read(), "png", File("atlas.png"))
	}

	override fun update(delta: Float) {
		super.update(delta)
		if (window.resized)
			resetCameraProjection()

		time += delta

		val wireframeButton = input[Key.F]
		if (wireframeButton.pressed) {
			glCall { GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE) }
			glCall { GL11.glDisable(GL11.GL_CULL_FACE) }
		} else if (wireframeButton.released) {
			glCall { GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL) }
			glCall { GL11.glEnable(GL11.GL_CULL_FACE) }
		}

		if (input[Key.F9].pressed) {
			ImageIO.write(atlas.texture.read(), "png", File("block_item_atlas.png"))
			ImageIO.write(GUIManager.atlas.texture.read(), "png", File("gui_atlas.png"))
		}

		GUIManager.update(delta)
	}

	override fun render(delta: Float) {
		val sky = (Minecraft.world.proxy as ClientWorldProxy).sky
		sky.render(camera, delta)
		scene.render(delta, camera, BlankShader.shader)

		chunkShader.bind {
			uniforms["uLightDirection"] = sky.sun.direction
			uniforms["uModel"] = Matrix4f.identity()
			uniforms["uViewProj"] = camera.viewProjectionMatrix
			atlas.texture.bind(0)
			uniforms["uTexture"] = 0
			chunkModel.render()
		}

		GUIManager.render(delta)
	}

	companion object : ProxyHolder<MinecraftClient> {

		override val proxy: MinecraftClient
			get() = SparseEngine.game as MinecraftClient

	}
}