package blue.sparse.minecraft.common.entity

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.entity.data.EntityData
import blue.sparse.minecraft.common.world.World

class Entity<out T : EntityType>(val type: T, var position: Vector3f, var world: World) {

	var timeSinceSpawned = 0f
		private set

	var velocity = Vector3f(0f)

    val data = type.createData()

    fun despawn(): Boolean {
		timeSinceSpawned = 0f
		return world.despawnEntity(this)
	}

    fun spawn(): Boolean {
		return world.spawnEntity(this)
	}

    fun update(delta: Float) {
		timeSinceSpawned += delta

		velocity.y -= 16f * delta

//		val drag = 0.02f
//		velocity.timesAssign(Math.pow(drag.toDouble(), delta.toDouble()).toFloat())
//		velocity = clamp(velocity, -78.4f, 78.4f)

		val bounds = type.bounds
		val movement = velocity * delta
		val unaffected = world.testBlockIntersections(bounds, position, movement)

//		bounds.debugRender(position, Vector3f(1f))

		velocity = movement / delta

		if(unaffected.any { it == 0f }) {

			val affected = Vector3f(1f) - unaffected
			val friction = unaffected * 0.8f
			friction += affected

			velocity.timesAssign(friction)

		}
		position.plusAssign(movement)

        type.update(this, delta)
    }

    inline fun <reified T: EntityData> editData(body: T.() -> Unit) {
        if(data !is T)
            throw TypeCastException("${data::class.qualifiedName} cannot be cast to ${T::class.qualifiedName}")

		data.apply(body)
	}

    @Suppress("UNCHECKED_CAST")
    inline fun <reified N : EntityType> safeCast(): Entity<N>? {
        if (!N::class.isInstance(type))
            return null
        return this as Entity<N>
    }

    inline fun <reified N : EntityType> typeCast(): Entity<N> {
        return safeCast() ?: throw TypeCastException("${type::class.qualifiedName} is not a subclass of ${N::class.qualifiedName}")
    }
}