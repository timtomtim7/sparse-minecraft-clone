package blue.sparse.minecraft.client.item

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.render.camera.Camera
import blue.sparse.engine.render.resource.Texture
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.engine.render.scene.component.Transformed
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.floats.vectorFromIntRGB
import blue.sparse.minecraft.client.MinecraftClient
import blue.sparse.minecraft.client.item.proxy.ClientItemTypeProxy
import blue.sparse.minecraft.client.world.proxy.ClientWorldProxy
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.item.impl.types.ItemTypeBlock
import blue.sparse.minecraft.common.util.random

class ItemComponent(val item: Item<*>, val position: Vector3f) : Transformed() {

	private var time: Float = random.nextFloat() * 100f

	private lateinit var color: Vector3f
	private lateinit var enchantColor: Vector3f

	override val overridesShader = true

	init {
		transform.rotateDeg(Vector3f(0f, 1f, 0f), random.nextFloat() * 360)
		transform.setScale(if(item.type is ItemTypeBlock) Vector3f(4f / 16f) else Vector3f(8f / 16f))

		refresh()
	}

	fun refresh() {
		color = item.color.vectorFromIntRGB()
		enchantColor = if(item.enchanted) item.enchantColor.vectorFromIntRGB() else Vector3f(0f)
	}

	override fun update(delta: Float) {
		transform.rotateDeg(Vector3f(0f, 1f, 0f), delta * 40f)

		transform.setTranslation(position + Vector3f(0f, (Math.sin(time.toDouble()).toFloat() * 0.5f + 0.5f) * (4f / 16f), 0f))

		time += delta
	}

	override fun render(delta: Float, camera: Camera, shader: ShaderProgram) {
		Companion.shader.bind {
			uniforms["uLightDirection"] = (Minecraft.world.proxy as ClientWorldProxy).sky.sun.direction
			uniforms["uViewProj"] = camera.viewProjectionMatrix
			uniforms["uModel"] = modelMatrix
			uniforms["uColor"] = color
			uniforms["uEnchantTexture"] = 1
			enchantTexture.bind(1)
			uniforms["uEnchantColor"] = enchantColor
			uniforms["uEnchantTime"] = time * 0.2f

			(Minecraft.proxy as MinecraftClient).atlas.texture.bind(0)
			(item.type.proxy as ClientItemTypeProxy).model.render()
		}
	}

	companion object {
		val enchantTexture = Texture(Asset["minecraft/textures/misc/enchanted_item_glint.png"])

		val shader = ShaderProgram(Asset["minecraft/shaders/item.fs"], Asset["minecraft/shaders/item.vs"])
	}
}