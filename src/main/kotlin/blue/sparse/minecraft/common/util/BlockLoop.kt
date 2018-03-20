package blue.sparse.minecraft.common.util

import blue.sparse.math.vectors.floats.lengthSquared
import blue.sparse.math.vectors.ints.Vector3i

object BlockLoop {

	private val cache = ArrayList<Vector3i>()

	init {
		for (x in -10..10) {
			for (y in -10..10) {
				for (z in -10..10) {
					cache.add(Vector3i(x, y, z))
				}
			}
		}

		cache.sortBy { lengthSquared(it.toFloatVector()) }
	}

	operator fun get(i: Int, center: Vector3i): Vector3i {
		return cache[i] + center
	}

}