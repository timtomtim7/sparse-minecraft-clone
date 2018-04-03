package blue.sparse.minecraft.common.util.math

import blue.sparse.math.vectors.floats.Vector2f
import blue.sparse.math.vectors.floats.lengthSquared
import blue.sparse.minecraft.common.util.getValue
import blue.sparse.minecraft.common.util.threadLocal
import java.util.Random

object Voronoi {

	private val random by threadLocal(::Random)

	operator fun get(x: Float, y: Float): Vector2f {
		val point = Vector2f(x, y)

		val cx = x.toInt() shr BITS
		val cy = y.toInt() shr BITS

		var closest: Vector2f? = null
		var len = 0f

		for (i in -1..1) {
			for (j in -1..1) {
				val v = getClosestPointInCube(cx + i, cy + j, point)
				val vLen = distance(v, point)
				if (closest == null || vLen < len) {
					closest = v
					len = vLen
				}
			}
		}

//		return distance(point, closest!!) / SIZE_FLOAT
		return closest!!
	}

	private fun getClosestPointInCube(cx: Int, cy: Int, point: Vector2f): Vector2f {
		var id = 7L
		id = 73L * id + cx
		id = 73L * id + cy

		val random = random
		random.setSeed(id)

		val pointCount = random.nextInt(2) + 1

		val cubeOrigin = Vector2f(
				(cx shl BITS).toFloat(),
				(cy shl BITS).toFloat()
		)

		var closest: Vector2f? = null
		var len = 0f

		for (i in 1..pointCount) {

			val v = Vector2f(
					cubeOrigin.x + random.nextFloat() * SIZE_FLOAT,
					cubeOrigin.y + random.nextFloat() * SIZE_FLOAT
			)

			val vLen = distance(v, point)
			if (closest == null || vLen < len) {
				closest = v
				len = vLen
			}
		}

		return closest!!
	}

	private fun distance(a: Vector2f, b: Vector2f): Float {
		val dir = a - b
		val noise = Perlin.noise(dir.x + a.x, dir.y + a.y, 0f, 1, 1f, 0.05f) * 32f

		return lengthSquared(dir) + (noise * noise)
	}

	private const val BITS = 5
	private const val SIZE = 1 shl BITS
	private const val SIZE_FLOAT = SIZE.toFloat()
	private const val MASK = SIZE - 1
}