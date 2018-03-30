package blue.sparse.minecraft.common.entity

import blue.sparse.math.vectors.floats.*
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.entity.attribute.EntityAttribute
import blue.sparse.minecraft.common.entity.attribute.EntityAttributeType
import blue.sparse.minecraft.common.entity.data.EntityData
import blue.sparse.minecraft.common.entity.impl.types.living.EntityTypeLiving
import blue.sparse.minecraft.common.util.TargetBlock
import blue.sparse.minecraft.common.world.BlockView
import blue.sparse.minecraft.common.world.World

class Entity<out T : EntityType>(
		val type: T,
		var world: World,
		var position: Vector3f = Vector3f(0f, 0f, 0f),
		var rotation: Quaternion4f = Quaternion4f()
) {

	private val attributes = HashMap<EntityAttributeType<*, *>, EntityAttribute<*>>()

	var lastPosition = position.clone()
		get() = field.clone()
		private set

	var lastRotation = rotation.clone()
		get() = field.clone()
		private set

	val interpolatedPosition: Vector3f
		get() = lerp(lastPosition, position, Minecraft.partialTicks)

	var velocity = Vector3f(0f)

	var timeSinceSpawned = 0f
		private set

    val data = type.createData()

	val blockPosition: Vector3i
		get() = floor(position).toIntVector()

	val block: BlockView
		get() = blockPosition.run { world.getOrGenerateBlock(x, y, z) }

	init {
		for (type in type.getAttributeTypes())
			attributes[type] = type.create(this)
	}

    fun add(): Boolean {
		return world.addEntity(this)
	}

	fun remove(): Boolean {
		if (world.removeEntity(this)) {
			timeSinceSpawned = 0f
			return true
		}
		return false
	}

	@Suppress("UNCHECKED_CAST")
	fun <V: Any, A: EntityAttribute<V>> getAttribute(type: EntityAttributeType<V, A>): A {
		return attributes[type] as A
	}

	operator fun <V: Any, A: EntityAttribute<V>> set(type: EntityAttributeType<V, A>, value: V): V {
		val attrib = getAttribute(type)
		attrib.value = value
		return attrib.value
	}

	operator fun <V: Any, A: EntityAttribute<V>> get(type: EntityAttributeType<V, A>): V {
		return getAttribute(type).value
	}

	infix fun has(type: EntityAttributeType<*, *>): Boolean {
		return type in attributes
	}

    fun update(delta: Float) {
		timeSinceSpawned += delta

		val drag = 0.4f
		var vel = velocity.clone()

		vel.timesAssign(Math.pow(drag.toDouble(), delta.toDouble()).toFloat())
		vel.y -= type.gravity * delta

//		velocity = clamp(velocity, -78.4f, 78.4f)

		val bounds = type.bounds
		val movement = vel * delta
		val unaffected = world.testBlockIntersections(bounds, position, movement)

//		bounds.debugRender(position, Vector3f(1f))

		vel = movement / delta
		lastPosition = position.clone()
		position.plusAssign(movement)

		if(unaffected.any { it == 0f }) {
			val affected = Vector3f(1f) - unaffected

			// Minecraft only does friction vertically
			affected.x = 0f
			affected.z = 0f

			val friction = unaffected * 0.005f
			friction += affected

			friction.x = Math.pow(friction.x.toDouble(), delta.toDouble()).toFloat()
			friction.y = Math.pow(friction.y.toDouble(), delta.toDouble()).toFloat()
			friction.z = Math.pow(friction.z.toDouble(), delta.toDouble()).toFloat()
			vel.timesAssign(friction)
		}

		velocity = vel

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