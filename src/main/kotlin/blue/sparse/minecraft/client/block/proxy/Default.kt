package blue.sparse.minecraft.client.block.proxy

import blue.sparse.engine.asset.Asset
import blue.sparse.minecraft.common.block.BlockType

class Default(blockType: BlockType) : ClientBlockTypeProxy(blockType) {
	override val frontTexture = Asset["minecraft/textures/blocks/${blockType.identifier.name}.png"]
}