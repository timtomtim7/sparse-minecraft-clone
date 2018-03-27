package blue.sparse.minecraft.client.entity.render

import blue.sparse.math.FloatTransform
import blue.sparse.math.vectors.floats.lerp
import blue.sparse.math.vectors.floats.nLerp

data class Pose(val transforms: Map<String, FloatTransform>) : Map<String, FloatTransform> by transforms {

	fun lerp(otherPose: Pose, targetPose: Pose, amount: Float) {
		for ((key, target) in targetPose) {
			val other = otherPose[key] ?: continue
			val self = this[key] ?: FloatTransform()

			target.setTranslation(lerp(self.translation, other.translation, amount))
			target.setScale(lerp(self.scale, other.scale, amount))
			target.setRotation(nLerp(self.rotation, other.rotation, amount))
		}
	}

	fun copyTo(other: Pose) {

		for((k, v) in this) {
			val target = other[k] ?: continue

			target.setTranslation(v.translation)
			target.setRotation(v.rotation)
			target.setScale(v.scale)
		}

	}

}