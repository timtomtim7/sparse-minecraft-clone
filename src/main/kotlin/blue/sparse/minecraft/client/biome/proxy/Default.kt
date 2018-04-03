package blue.sparse.minecraft.client.biome.proxy

import blue.sparse.engine.asset.Asset
import blue.sparse.math.clamp
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.floats.vectorFromIntRGB
import blue.sparse.minecraft.common.biome.BiomeType

class Default(biomeType: BiomeType) : ClientBiomeTypeProxy(biomeType) {
	override val color: Vector3f = getColor(biomeType.temperature, biomeType.rainfall)

	companion object {

		private val colors = Asset["minecraft/textures/colormap/grass.png"].readImage()

		fun getColor(temperature: Float, rainfall: Float): Vector3f {
			val adjTemp = clamp(temperature, 0f, 1f)
			val adjRainfall = clamp(rainfall, 0f, 1f) * adjTemp

			val x = ((1f - adjTemp) * (colors.width - 1)).toInt()
			val y = ((1f - adjRainfall) * (colors.height - 1)).toInt()

			val rgb = colors.getRGB(x, y)

			return rgb.vectorFromIntRGB()
		}

	}
}