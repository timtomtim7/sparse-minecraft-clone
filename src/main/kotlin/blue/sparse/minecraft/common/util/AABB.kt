package blue.sparse.minecraft.common.util

import blue.sparse.math.vectors.floats.Vector3f

class AABB(min: Vector3f, max: Vector3f) {
	val min: Vector3f = min
		get() = field.clone()

	val max: Vector3f = max
		get() = field.clone()

	fun intersects(other: AABB) {

	}
}