package blue.sparse.minecraft.common.util

import blue.sparse.math.abs
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.client.util.Debug

class AABB(min: Vector3f, max: Vector3f) {
	val min: Vector3f = min
		get() = field.clone()

	val max: Vector3f = max
		get() = field.clone()

	fun testIntersection(thisPosition: Vector3f, movement: Vector3f, other: AABB, otherPosition: Vector3f): Vector3f {
		// This bounding box but moved into position
		val thisMin = min + thisPosition
		val thisMax = max + thisPosition

		// The bounding box you might collide with
		val otherMin = other.min + otherPosition
		val otherMax = other.max + otherPosition

		// The axes that are unaffected by this test will have a value of 1, whereas axes that have been affected will have a value of 0.
		// To begin with, we haven't modified any axes, so they're all 1.
		val unaffectedAxes = Vector3f(1f)

		// The axes to detect collision on. This is 3D, so the first 3
		val axes = arrayListOf(0, 1, 2) // "axes" is the plural of "axis"

		// Sort the axes by which would result in the least movement if it were being collided with.
		axes.sortBy { abs(axisCollisionOffset(thisMin[it], thisMax[it], otherMin[it], otherMax[it], movement[it])) }

		// Keep track of which movement axes have already been modified so that future axes will take those into account aswell.
		val modifiedMovement = Vector3f(0f)

		for (axis in axes) {
			// Generate a vector pointing in the direction of the axis
			val axisVector = Vector3f(0f)
			axisVector[axis] = 1f

			// The bounding box where you will be after moving in this axis and previous axes.
			val modifiedMin = thisMin - (modifiedMovement + movement * axisVector)
			val modifiedMax = thisMax - (modifiedMovement + movement * axisVector)

			if (intersects(modifiedMin, modifiedMax, otherMin, otherMax)) {
				// The offset from the collided axis
				val offset = axisCollisionOffset(thisMin[axis], thisMax[axis], otherMin[axis], otherMax[axis], movement[axis])

				// Move it by the offset so that it is perfectly on the boundary of colliding
				movement[axis] = offset
				modifiedMovement[axis] = offset
				unaffectedAxes[axis] = 0f
			}
		}

		return unaffectedAxes
	}

	private fun intersects(aMin: Vector3f, aMax: Vector3f, bMin: Vector3f, bMax: Vector3f): Boolean {
		return aMin.x < bMax.x && aMax.x > bMin.x &&
				aMin.y < bMax.y && aMax.y > bMin.y &&
				aMin.z < bMax.z && aMax.z > bMin.z
	}

	private fun axisCollisionOffset(aMin: Float, aMax: Float, bMin: Float, bMax: Float, movement: Float): Float {
		if (movement != 0f) {
			val dest: Float
			val from: Float
			if (movement > 0f) {
				dest = bMax + 0.001f
				from = aMin
			} else {
				dest = bMin - 0.001f
				from = aMax
			}
			val diff = from - dest
			return diff
		}
		return 0f
	}

	internal fun debugRender(position: Vector3f, color: Vector3f = Vector3f(1f, 0f, 0f)) {
		Debug.addTempCube(min + position, max + position, color)
	}

	companion object {
		fun ofSize(width: Float, height: Float): AABB {
			val vector = Vector3f(width, height, width)
			return AABB(vector / -2f, vector / 2f)
		}
	}
}