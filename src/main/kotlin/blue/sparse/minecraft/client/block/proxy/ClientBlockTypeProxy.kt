package blue.sparse.minecraft.client.block.proxy

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.render.resource.model.Model
import blue.sparse.minecraft.client.item.render.BlockItemModelGenerator
import blue.sparse.minecraft.client.world.render.WorldRenderer
import blue.sparse.minecraft.common.block.BlockType

abstract class ClientBlockTypeProxy(blockType: BlockType) : BlockType.BlockTypeProxy(blockType) {

	abstract val frontTexture: Asset
	open val backTexture: Asset get() = frontTexture
	open val topTexture: Asset get() = frontTexture
	open val bottomTexture: Asset get() = frontTexture
	open val leftTexture: Asset get() = frontTexture
	open val rightTexture: Asset get() = frontTexture

	val frontSprite by lazy { WorldRenderer.atlas.getOrAddSprite(frontTexture) }
	val backSprite by lazy { WorldRenderer.atlas.getOrAddSprite(backTexture) }
	val topSprite by lazy { WorldRenderer.atlas.getOrAddSprite(topTexture) }
	val bottomSprite by lazy { WorldRenderer.atlas.getOrAddSprite(bottomTexture) }
	val leftSprite by lazy { WorldRenderer.atlas.getOrAddSprite(leftTexture) }
	val rightSprite by lazy { WorldRenderer.atlas.getOrAddSprite(rightTexture) }

	open fun generateItemModel(): Model? {
		if (blockType.hasItem) {
//			val texture = (blockType.item!!.proxy as ClientItemTypeProxy).texture
			val atlas = WorldRenderer.atlas
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