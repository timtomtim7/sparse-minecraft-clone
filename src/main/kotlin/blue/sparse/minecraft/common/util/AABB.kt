package blue.sparse.minecraft.common.util

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.client.util.Debug

class AABB(min: Vector3f, max: Vector3f) {
	val min: Vector3f = min
		get() = field.clone()

	val max: Vector3f = max
		get() = field.clone()

	fun testIntersection(thisPosition: Vector3f, movement: Vector3f, other: AABB, otherPosition: Vector3f): Vector3f {
		// the bounding box where you are
		val aMin = min + thisPosition
		val aMax = max + thisPosition

		// the bounding box you might collide with
		val bMin = other.min + otherPosition
		val bMax = other.max + otherPosition

		// the bounding box where you will be on X
		val mxMin = aMin - Vector3f(movement.x, 0f, 0f)
		val mxMax = aMax - Vector3f(movement.x, 0f, 0f)

		if (intersects(mxMin, mxMax, bMin, bMax))
			movement.x = axisCollide(aMin.x, aMax.x, bMin.x, bMax.x, movement.x)

		// the bounding box where you will be on Y
		val myMin = aMin - Vector3f(movement.x, movement.y, 0f)
		val myMax = aMax - Vector3f(movement.x, movement.y, 0f)

		if (intersects(myMin, myMax, bMin, bMax))
			movement.y = axisCollide(aMin.y, aMax.y, bMin.y, bMax.y, movement.y)

		// the bounding box where you will be on Z
		val mzMin = aMin - Vector3f(movement.x, movement.y, movement.z)
		val mzMax = aMax - Vector3f(movement.x, movement.y, movement.z)

		if (intersects(mzMin, mzMax, bMin, bMax))
			movement.z = axisCollide(aMin.z, aMax.z, bMin.z, bMax.z, movement.z)

//		val mxyMin = aMin - Vector3f(movement.x, movement.y, 0f)
//		val mxyMax = aMax - Vector3f(movement.x, movement.y, 0f)
//		if (intersects(mxyMin, mxyMax, bMin, bMax))
//			movement.x = axisCollide(aMin.x, aMax.x, bMin.x, bMax.x, movement.x)
//
//		val myzMin = aMin - Vector3f(0f, movement.y, movement.z)
//		val myzMax = aMax - Vector3f(0f, movement.y, movement.z)
//		if (intersects(myzMin, myzMax, bMin, bMax))
//			movement.y = axisCollide(aMin.y, aMax.y, bMin.y, bMax.y, movement.y)
//
//		val mxzMin = aMin - Vector3f(movement.x, 0f, movement.z)
//		val mxzMax = aMax - Vector3f(movement.x, 0f, movement.z)
//		if (intersects(mxzMin, mxzMax, bMin, bMax))
//			movement.z = axisCollide(aMin.z, aMax.z, bMin.z, bMax.z, movement.z)

//		val fMin = aMin - movement
//		val fMax = aMax - movement
//		if (intersects(fMin, fMax, bMin, bMax)) {
//			movement.x = axisCollide(aMin.x, aMax.x, bMin.x, bMax.x, movement.x)
//			movement.y = axisCollide(aMin.y, aMax.y, bMin.y, bMax.y, movement.y)
//			movement.z = axisCollide(aMin.z, aMax.z, bMin.z, bMax.z, movement.z)
//		}

		return movement
	}

	private fun intersects(aMin: Vector3f, aMax: Vector3f, bMin: Vector3f, bMax: Vector3f): Boolean {
		return  aMin.x < bMax.x && aMax.x > bMin.x &&
				aMin.y < bMax.y && aMax.y > bMin.y &&
				aMin.z < bMax.z && aMax.z > bMin.z
	}

	private fun axisCollide(aMin: Float, aMax: Float, bMin: Float, bMax: Float, movement: Float): Float {
		if(movement != 0f) {
			val dest: Float
			val from: Float
			if (movement > 0f) {
				dest = bMax
				from = aMin
			} else {
				dest = bMin
				from = aMax
			}
			val diff = from - dest
			return diff
		}
		return 0f
	}

	internal fun debugRender(position: Vector3f, color: Vector3f = Vector3f(1f, 0f, 0f)) {
		val min = min + position
		val max = max + position

		Debug.drawLine(min, Vector3f(max.x, min.y, min.z), color)
		Debug.drawLine(min, Vector3f(min.x, max.y, min.z), color)
		Debug.drawLine(min, Vector3f(min.x, min.y, max.z), color)

		Debug.drawLine(max, Vector3f(min.x, max.y, max.z), color)
		Debug.drawLine(max, Vector3f(max.x, min.y, max.z), color)
		Debug.drawLine(max, Vector3f(max.x, max.y, min.z), color)

//		Debug.drawLine(Vector3f(min.x, min.y, min.z), Vector3f(min.x, min.z, min.z))
//
//		Debug.drawLine(Vector3f(min.x, min.y, min.z), Vector3f(min.x, min.z, min.z))
//		Debug.drawLine(Vector3f(min.x, min.y, min.z), Vector3f(min.x, min.z, min.z))
//		Debug.drawLine(Vector3f(min.x, min.y, min.z), Vector3f(min.x, min.z, min.z))
//		Debug.drawLine(Vector3f(min.x, min.y, min.z), Vector3f(min.x, min.z, min.z))
//
//		Debug.drawLine(Vector3f(min.x, min.y, min.z), Vector3f(min.x, min.z, min.z))
//		Debug.drawLine(Vector3f(min.x, min.y, min.z), Vector3f(min.x, min.z, min.z))
//		Debug.drawLine(Vector3f(min.x, min.y, min.z), Vector3f(min.x, min.z, min.z))
//		Debug.drawLine(Vector3f(min.x, min.y, min.z), Vector3f(min.x, min.z, min.z))
	}
}