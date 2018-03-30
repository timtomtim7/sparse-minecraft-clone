package blue.sparse.minecraft.common.entity.impl.types.living

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.util.math.AABB

object EntityTypePlayer : EntityTypeLiving("player") {
	override val bounds = AABB(Vector3f(-0.3f, 0f, -0.3f), Vector3f(0.3f, 1.8f, 0.3f))

	override val gravity: Float = 32f
}