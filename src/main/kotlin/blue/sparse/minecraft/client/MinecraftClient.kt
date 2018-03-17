package blue.sparse.minecraft.client

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.SparseGame
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.render.camera.FirstPerson
import blue.sparse.engine.window.input.Key
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.ints.Vector2i
import blue.sparse.minecraft.client.gui.GUIManager
import blue.sparse.minecraft.client.gui.TestGUI
import blue.sparse.minecraft.client.item.ItemComponent
import blue.sparse.minecraft.client.sky.OverworldSky
import blue.sparse.minecraft.client.util.BlankShader
import blue.sparse.minecraft.common.MinecraftProxy
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.ProxyHolder
import org.lwjgl.opengl.GL11

class MinecraftClient : SparseGame(), MinecraftProxy {

	val atlas = TextureAtlas(Vector2i(1024, 1024))

	val sky = OverworldSky()

	private var time = 0f

	init {
		camera.apply {
			move(Vector3f(0f, 0f, 1f))
			controller = FirstPerson(this)
		}
	}

	fun spawnItem(item: Item<*>, position: Vector3f) {
		scene.add(ItemComponent(item, position))
	}

	override fun postInit() {
		ItemType
		BlockType

//		val item = Item(ItemType.ironChestplate)
//		item.enchantColor = 0x00FF00
//		item.editNBT { list("ench", emptyList()) }
//		spawnItem(item, Vector3f(0f))

		GUIManager.open(TestGUI)

		val itemTypes = ItemType.registry.values

		val squareSize = Math.ceil(Math.sqrt(itemTypes.size.toDouble())).toInt()

		for((i, itemType) in itemTypes.withIndex()) {
			val x = i % squareSize
			val y = i / squareSize
			val item = Item(itemType)

			spawnItem(item, Vector3f(x.toFloat(), y.toFloat(), 0f) * 0.8f)
		}

//		ImageIO.write(atlas.texture.read(), "png", File("atlas.png"))
	}

	override fun update(delta: Float) {
		super.update(delta)

		time += delta

		val wireframeButton = input[Key.F]
		if (wireframeButton.pressed) {
			glCall { GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE) }
			glCall { GL11.glDisable(GL11.GL_CULL_FACE) }
		} else if (wireframeButton.released) {
			glCall { GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL) }
			glCall { GL11.glEnable(GL11.GL_CULL_FACE) }
		}
	}

	override fun render(delta: Float) {
		sky.render(camera, delta)
		scene.render(delta, camera, BlankShader.shader)
		GUIManager.render(delta)
	}

	companion object : ProxyHolder<MinecraftClient> {

		override val proxy: MinecraftClient
			get() = SparseEngine.game as MinecraftClient

	}
}