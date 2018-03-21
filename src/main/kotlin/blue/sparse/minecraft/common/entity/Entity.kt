package blue.sparse.minecraft.common.entity

import blue.sparse.math.vectors.floats.*
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.entity.data.EntityData
import blue.sparse.minecraft.common.entity.impl.types.living.EntityTypeLiving
import blue.sparse.minecraft.common.util.TargetBlock
import blue.sparse.minecraft.common.world.BlockView
import blue.sparse.minecraft.common.world.World

class Entity<out T : EntityType>(val type: T, var world: World, var position: Vector3f = Vector3f(0f, 0f, 0f), var rotation: Quaternion4f = Quaternion4f()) {

	var timeSinceSpawned = 0f
		private set

	var velocity = Vector3f(0f)

    val data = type.createData()

	val blockPosition: Vector3i
		get() = floor(position).toIntVector()

	val block: BlockView
		get() = blockPosition.run { world.getOrGenerateBlock(x, y, z) }

    fun despawn(): Boolean {
		timeSinceSpawned = 0f
		return world.despawnEntity(this)
	}

    fun spawn(): Boolean {
		return world.spawnEntity(this)
	}

    fun update(delta: Float) {
		timeSinceSpawned += delta

		val drag = 0.4f
		velocity.timesAssign(Math.pow(drag.toDouble(), delta.toDouble()).toFloat())
		velocity.y -= type.gravity * delta
//		velocity = clamp(velocity, -78.4f, 78.4f)

		val bounds = type.bounds
		val movement = velocity * delta
		val unaffected = world.testBlockIntersections(bounds, position, movement)

//		bounds.debugRender(position, Vector3f(1f))

		velocity = movement / delta
		position.plusAssign(movement)

		if(unaffected.any { it == 0f }) {

			val affected = Vector3f(1f) - unaffected
			val friction = unaffected * 0.9f
			friction += affected

			velocity.timesAssign(friction)

		}

        type.update(this, delta)
    }

	fun getTargetBlock(maxDistance: Float): TargetBlock? {
		val eyeHeight = if(type is EntityTypeLiving) type.eyeHeight else 0f

		val origin = position + Vector3f(0f, eyeHeight, 0f)
		val direction = rotation.forward

		return world.getTargetBlock(origin, direction, maxDistance)
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