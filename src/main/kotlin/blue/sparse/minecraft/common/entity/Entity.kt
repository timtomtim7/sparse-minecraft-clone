package blue.sparse.minecraft.common.entity

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.world.World

data class Entity<out T : EntityType>(val type: T, var position: Vector3f, var world: World) {
    
    fun onTick() {}

    fun despawn(): Boolean = world.despawnEntity(this)

    fun spawn(): Boolean = world.spawnEntity(this)

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