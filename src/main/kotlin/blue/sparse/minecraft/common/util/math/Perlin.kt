package blue.sparse.minecraft.common.util.math

import org.lwjgl.stb.STBPerlin

object Perlin
{
	fun noise(x: Float, y: Float, z: Float, octaves: Int, persistence: Float, scale: Float): Float
	{
		var total = 0.0f
		var freq = scale
		var ampl = 1.0f

		var max = 0.0f
		for(i in 1..octaves)
		{
			total += STBPerlin.stb_perlin_noise3(x * freq, y * freq, z * freq, 1024, 1024, 1024) * ampl

			max += ampl
			ampl *= persistence
			freq *= 2f
		}

		return total / max
	}
}