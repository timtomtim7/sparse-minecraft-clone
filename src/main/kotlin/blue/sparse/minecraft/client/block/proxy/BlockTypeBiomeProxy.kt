package blue.sparse.minecraft.client.block.proxy

import blue.sparse.engine.asset.Asset
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.client.biome.proxy.ClientBiomeTypeProxy
import blue.sparse.minecraft.common.biome.BiomeType
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.world.World

class BlockTypeBiomeProxy(blockType: BlockType) : ClientBlockTypeProxy(blockType) {
	override val frontTexture = Asset["${blockType.identifier.namespace}/textures/blocks/${blockType.identifier.name}.png"]

	override fun getColor(world: World, x: Int, y: Int, z: Int): Vector3f {
		val biome = world.getBlock(x, y, z)?.biome ?: BiomeType.void

		return (biome.proxy as ClientBiomeTypeProxy).color
	}
}