package blue.sparse.minecraft.client.biome.proxy

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.biome.BiomeType

abstract class ClientBiomeTypeProxy(biomeType: BiomeType): BiomeType.BiomeTypeProxy(biomeType) {

	abstract val color: Vector3f

}