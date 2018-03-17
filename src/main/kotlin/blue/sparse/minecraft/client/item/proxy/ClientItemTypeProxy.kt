package blue.sparse.minecraft.client.item.proxy

import blue.sparse.engine.asset.Asset
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.client.MinecraftClient
import blue.sparse.minecraft.client.item.render.ItemModelGenerator
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.item.ItemType

abstract class ClientItemTypeProxy(itemType: ItemType) : ItemType.ItemTypeProxy(itemType) {
	abstract val texture: Asset

	open fun getColor(item: Item<*>): Vector3f {
		val displayCompound = item.data?.compound("display") ?: return Vector3f(1f)
		val colorCompound = displayCompound.compound("color") ?: return Vector3f(1f)

		return Vector3f(
				colorCompound.float("r") ?: 0f,
				colorCompound.float("g") ?: 0f,
				colorCompound.float("b") ?: 0f
		)
	}

	open val model by lazy {
		val image = texture.readImage()
		val atlas = (Minecraft.proxy as MinecraftClient).atlas
		val sprite = atlas.getOrAddSprite(texture.path, image)

		ItemModelGenerator.generateModel(image, sprite.textureCoords, sprite.atlas.size)
	}

//	companion object {
//		val defaultModel = run {
//			val texture = Asset["minecraft/textures/blocks/debug.png"]
//			val image = texture.readImage()
//			val atlas = (Minecraft.proxy as MinecraftClient).atlas
//			val sprite = atlas.getOrAddSprite(texture.path, image)
//
//			ItemModelGenerator.generateModel(image, sprite.textureCoords, sprite.atlas.size)
//		}
//	}
}