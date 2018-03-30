package blue.sparse.minecraft.common.entity

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.entity.attribute.EntityAttributeType
import blue.sparse.minecraft.common.entity.data.EntityData
import blue.sparse.minecraft.common.entity.impl.types.EntityTypeItem
import blue.sparse.minecraft.common.entity.impl.types.living.hostile.undead.zombie.EntityTypeZombie
import blue.sparse.minecraft.common.util.Identifier
import blue.sparse.minecraft.common.util.math.AABB
import blue.sparse.minecraft.common.util.proxy.Proxy
import blue.sparse.minecraft.common.util.proxy.ProxyProvider

abstract class EntityType(val identifier: Identifier) {

	abstract class EntityTypeProxy(val entityType: EntityType) : Proxy

	open val proxy: EntityTypeProxy by ProxyProvider<EntityTypeProxy>(
			"blue.sparse.minecraft.client.entity.proxy.Default",
			"blue.sparse.minecraft.server.entity.proxy.Default",
			this
	)

	open val bounds = AABB(Vector3f(-0.5f), Vector3f(0.5f))

	open val gravity: Float = 16f

	init {
        register(this)
	}

	constructor(id: String) : this(Identifier(id))

	open fun update(entity: Entity<*>, delta: Float) {}

	open fun createData(): EntityData {
		return EntityData.Default
	}

	open fun getAttributeTypes(): List<EntityAttributeType<*, *>> {
		return emptyList()
	}

	companion object {
		internal val registry = LinkedHashMap<Identifier, EntityType>()

		private fun register(type: EntityType) {
			if (type.identifier in registry)
				throw IllegalArgumentException("Entity with identifier \"${type.identifier}\" is already registered.")

			registry[type.identifier] = type
		}

		val item = EntityTypeItem
		val zombie = EntityTypeZombie
	}
}