package blue.sparse.minecraft.client.entity.render

import blue.sparse.math.FloatTransform
import blue.sparse.math.vectors.floats.lerp
import blue.sparse.math.vectors.floats.nLerp

data class Pose(val transforms: Map<String, FloatTransform>) : Map<String, FloatTransform> by transforms {

	override operator fun get(key: String): FloatTransform {
		return transforms[key]!!
	}

	fun lerp(otherPose: Pose, targetPose: Pose, amount: Float) {
		for ((key, target) in targetPose) {
			if(key !in this || key !in otherPose)
				continue

			val other = otherPose[key]
			val self = this[key]

			target.setTranslation(lerp(self.translation, other.translation, amount))
			target.setScale(lerp(self.scale, other.scale, amount))
			target.setRotation(nLerp(self.rotation, other.rotation, amount))
		}
	}

	fun copyTo(other: Pose) {
		for ((k, v) in this) {
			if (k !in other) continue
			val target = other[k]

			target.setTranslation(v.translation)
			target.setRotation(v.rotation)
			target.setScale(v.scale)
		}
	}
}