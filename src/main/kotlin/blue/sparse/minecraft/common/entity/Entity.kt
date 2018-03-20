package blue.sparse.minecraft.common.entity

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.entity.data.EntityData
import blue.sparse.minecraft.common.world.World

data class Entity<out T : EntityType>(val type: T, var position: Vector3f, var world: World) {

	var timeSinceSpawned = 0f
		private set

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