package blue.sparse.minecraft.common.util.math

import blue.sparse.math.vectors.ints.Vector3i

enum class BlockFace(val x: Int, val y: Int, val z: Int) {
	POSITIVE_X(1, 0, 0),
	POSITIVE_Y(0, 1, 0),
	POSITIVE_Z(0, 0, 1),
	NEGATIVE_X(-1, 0, 0),
	NEGATIVE_Y(0, -1, 0),
	NEGATIVE_Z(0, 0, -1);

	val offset: Vector3i
		get() = Vector3i(x, y, z)

	companion object {
		operator fun get(vec: Vector3i): BlockFace? {
			return values().find { it.x == vec.x && it.y == vec.y && it.z == vec.z }
		}
	}
}