package blue.sparse.minecraft.client.entity.proxy

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.render.camera.Camera
import blue.sparse.engine.render.resource.Texture
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.math.FloatTransform
import blue.sparse.math.vectors.floats.*
import blue.sparse.minecraft.client.item.proxy.ClientItemTypeProxy
import blue.sparse.minecraft.client.world.proxy.ClientWorldProxy
import blue.sparse.minecraft.client.world.render.WorldRenderer
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.entity.impl.types.EntityTypeItem
import blue.sparse.minecraft.common.item.impl.types.ItemTypeBlock

class ClientEntityTypeItemProxy(type: EntityType) : ClientEntityTypeProxy(type) {

	init {
		if (type != EntityTypeItem)
			throw IllegalArgumentException("Attempted to construct ClientEntityTypeItemProxy with invalid EntityType")
	}

	override fun render(entity: Entity<*>, camera: Camera, delta: Float) {
		val data = entity.data as EntityTypeItem.Data
		val item = data.stack.item

		transform.setScale(if (item.type is ItemTypeBlock) blockScale else itemScale)
		transform.setRotation(Quaternion4f(rotationAxis, Math.toRadians(entity.timeSinceSpawned * 40.0).toFloat()))
		val bobbing = (Math.sin(entity.timeSinceSpawned.toDouble() * 1.25).toFloat() * 0.5f + 0.5f) * (4f / 16f) + (2f / 16f)

		transform.setTranslation(entity.interpolatedPosition + Vector3f(0f, bobbing, 0f))

		shader.bind {
			uniforms["uLightDirection"] = (Minecraft.world.proxy as ClientWorldProxy).sky.sun.direction
			uniforms["uViewProj"] = camera.viewProjectionMatrix
			uniforms["uModel"] = transform.matrix
			uniforms["uColor"] = item.color.vectorFromIntRGB()
			uniforms["uEnchantTexture"] = 1
			enchantTexture.bind(1)
			uniforms["uEnchantColor"] = item.enchantColor.vectorFromIntRGB()
			uniforms["uEnchantTime"] = 0f

			WorldRenderer.atlas.texture.bind(0)
			(item.type.proxy as ClientItemTypeProxy).model.render()
		}
	}

	companion object {
		private val transform = FloatTransform()
		private val rotationAxis = Vector3f(0f, 1f, 0f)
		private val itemScale = Vector3f(8f / 16f)
		private val blockScale = Vector3f(4f / 16f)

		val enchantTexture = Texture(Asset["minecraft/textures/misc/enchanted_item_glint.png"])

		val shader = ShaderProgram(Asset["minecraft/shaders/entity/item.fs"], Asset["minecraft/shaders/entity/item.vs"])
	}
}