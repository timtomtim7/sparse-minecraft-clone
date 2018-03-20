package blue.sparse.minecraft.client

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.SparseGame
import blue.sparse.engine.asset.Asset
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.engine.window.input.Key
import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.ints.Vector2i
import blue.sparse.minecraft.client.entity.proxy.ClientEntityTypeProxy
import blue.sparse.minecraft.client.gui.GUIManager
import blue.sparse.minecraft.client.gui.TestGUI
import blue.sparse.minecraft.client.item.ItemComponent
import blue.sparse.minecraft.client.util.BlankShader
import blue.sparse.minecraft.client.world.proxy.ClientChunkProxy
import blue.sparse.minecraft.client.world.proxy.ClientWorldProxy
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.MinecraftProxy
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.ProxyHolder
import blue.sparse.minecraft.common.util.random
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

//	lateinit var chunkModel: Model

	init {
		camera.apply {
			move(Vector3f(0f, 100f, 10f))
			controller = MinecraftController(this, movementSpeed = 10.92f)
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
		EntityType

		val world = Minecraft.world

//		val item = Item(ItemType.ironChestplate)
//		item.enchantColor = 0x00FF00
//		item.editNBT { list("ench", emptyList()) }
//
//		val entity = Entity(EntityTypeItem, Vector3f(20f), world)
//		entity.editData<EntityTypeItem.Data> {
//			stack = ItemStack(item)
//		}
//		world.spawnEntity(entity)

//		spawnItem(item, Vector3f(0f))

		GUIManager.open(TestGUI)


		for (x in -32..32) {
			for (z in -32..32) {
//				val maxY = ((Math.sin(x * 0.1) + Math.cos(z * 0.1)) * 8).toInt() + 16
//				val maxY = (STBPerlin.stb_perlin_noise3(x * 0.04f, z * 0.04f, 0f, 1024, 1024, 1024) * 16).toInt() + 16
				var maxY = 16
				if(random.nextDouble() < 0.05)
					maxY += 5
				for (y in 0..maxY) {
					world.getOrGenerateBlock(x, y, z).type = if(maxY - y < 4) BlockType.sand else BlockType.stone
				}
			}
		}

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
		Minecraft.world.update(delta)

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
			uniforms["uViewProj"] = camera.viewProjectionMatrix
			atlas.texture.bind(0)
			uniforms["uTexture"] = 0
//			uniforms["uModel"] = Matrix4f.identity()
			for (chunk in Minecraft.world.loadedChunks) {
				uniforms["uModel"] = Matrix4f.translation(chunk.worldBlockPosition.toFloatVector())
				val proxy = chunk.proxy as ClientChunkProxy
				if (proxy.model == null)
					proxy.generateOfflineModel()
				proxy.model?.render()
			}
//			chunkModel.render()
		}

		for (entity in Minecraft.world.entities) {
			val proxy = entity.type.proxy as ClientEntityTypeProxy
			proxy.render(entity, camera, delta)
		}

		val bounds = (camera.controller as MinecraftController).bounds
		bounds.debugRender(camera.transform.translation, Vector3f(1f, 0f, 0f))
		Minecraft.world.debugRenderInteresections(bounds, camera.transform.translation, Vector3f(0f, 1f, 0f))

		GUIManager.render(delta)
	}

	companion object : ProxyHolder<MinecraftClient> {

		override val proxy: MinecraftClient
			get() = SparseEngine.game as MinecraftClient

	}
}