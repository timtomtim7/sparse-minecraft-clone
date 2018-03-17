package blue.sparse.minecraft.client.block.proxy

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.render.resource.model.Model
import blue.sparse.minecraft.client.MinecraftClient
import blue.sparse.minecraft.client.item.render.BlockItemModelGenerator
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.block.BlockType

abstract class ClientBlockTypeProxy(blockType: BlockType) : BlockType.BlockTypeProxy(blockType) {

	abstract val frontTexture: Asset
	open val backTexture: Asset get() = frontTexture
	open val topTexture: Asset get() = frontTexture
	open val bottomTexture: Asset get() = frontTexture
	open val leftTexture: Asset get() = frontTexture
	open val rightTexture: Asset get() = frontTexture

	open fun generateItemModel(): Model? {
		if (blockType.hasItem) {
//			val texture = (blockType.item!!.proxy as ClientItemTypeProxy).texture
			val atlas = (Minecraft.proxy as MinecraftClient).atlas
//			val spriteLeft = atlas.getOrAddSprite(texture.path, texture)
			val spriteFront = atlas.getOrAddSprite(frontTexture)
			val spriteBack = atlas.getOrAddSprite(backTexture)
			val spriteTop = atlas.getOrAddSprite(topTexture)
			val spriteBottom = atlas.getOrAddSprite(bottomTexture)
			val spriteLeft = atlas.getOrAddSprite(leftTexture)
			val spriteRight = atlas.getOrAddSprite(rightTexture)

			return BlockItemModelGenerator.generateModel(BlockItemModelGenerator.TexCoords(
					spriteFront.textureCoords,
					spriteBack.textureCoords,
					spriteTop.textureCoords,
					spriteBottom.textureCoords,
					spriteLeft.textureCoords,
					spriteRight.textureCoords
			), atlas.size)
		}
		return null
	}

}