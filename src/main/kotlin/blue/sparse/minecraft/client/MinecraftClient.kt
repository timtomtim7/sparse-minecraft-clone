package blue.sparse.minecraft.client

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.SparseGame
import blue.sparse.engine.asset.Asset
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.render.camera.FirstPerson
import blue.sparse.engine.render.resource.Texture
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.engine.render.scene.component.ShaderSkybox
import blue.sparse.engine.window.input.Key
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.ints.Vector2i
import blue.sparse.minecraft.client.gui.GUIManager
import blue.sparse.minecraft.client.gui.TestGUI
import blue.sparse.minecraft.client.item.ItemComponent
import blue.sparse.minecraft.common.MinecraftProxy
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.ProxyHolder
import org.lwjgl.opengl.GL11

class MinecraftClient : SparseGame(), MinecraftProxy {

	val atlas = TextureAtlas(Vector2i(1024, 1024))
	val shader = ShaderProgram(Asset["minecraft/shaders/item.fs"], Asset["minecraft/shaders/item.vs"])

	private val sunTexture = Texture(Asset["minecraft/textures/environment/sun.png"]).apply {
		nearestFiltering()
		clampToEdge()
	}
	private val moonTexture = Texture(Asset["minecraft/textures/environment/moon_full.png"]).apply {
		nearestFiltering()
		clampToEdge()
	}

	val sun = CelestialBodyComponent(sunTexture, scale = 0.8f, initialRotation = -45f)
	val moon = CelestialBodyComponent(moonTexture, scale = 0.65f, initialRotation = -180f + -45f)

	private var time = 0f

	init {
		camera.apply {
			move(Vector3f(0f, 0f, 1f))
			controller = FirstPerson(this)
		}

		scene.add(ShaderSkybox(Asset["minecraft/shaders/sky.fs"]) {
			uniforms["uSunDirection"] = sun.transform.rotation.forward
			uniforms["uGravity"] = Vector3f(0f, -1f, 0f)
		})

		scene.add(moon)
		scene.add(sun)
	}

	fun spawnItem(item: Item<*>, position: Vector3f) {
		scene.add(ItemComponent(item, position))
	}

	override fun postInit() {
		ItemType
		BlockType

		val item = Item(ItemType.ironChestplate)
		item.enchantColor = 0x00FF00
		item.editNBT { list("ench", emptyList()) }
		spawnItem(item, Vector3f(0f))

		GUIManager.open(TestGUI)

//		for((i, itemType) in ItemType.registry.values.withIndex()) {
//			val x = i % 16
//			val y = i / 16
//			val item = Item(itemType)
////			item.color = Vector3f(random.nextFloat(), 0.75f, 1f).HSBtoRGB().toIntRGB()
////			item.enchantColor = Vector3f(random.nextFloat(), 1f, 1f).HSBtoRGB().toIntRGB()
////			item.editNBT { list("ench", emptyList()) }
//
//			spawnItem(item, Vector3f(x.toFloat(), y.toFloat(), 0f) * 0.8f)
//		}
//
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
		engine.clear()
		scene.render(delta, camera, shader)
		GUIManager.render(delta)
//		TextRenderer.drawString("Hello, world!", Vector3f(1f), Vector3f(1f), true, Matrix4f.identity(), camera.viewProjectionMatrix)
	}

	companion object : ProxyHolder<MinecraftClient> {

		override val proxy: MinecraftClient
			get() = SparseEngine.game as MinecraftClient

	}
}