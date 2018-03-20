package blue.sparse.minecraft.common.entity

import blue.sparse.minecraft.common.entity.data.EntityData
import blue.sparse.minecraft.common.entity.impl.types.EntityTypeItem
import blue.sparse.minecraft.common.entity.impl.types.living.hostile.undead.zombie.EntityTypeZombie
import blue.sparse.minecraft.common.util.*

abstract class EntityType(val identifier: Identifier) {

	abstract class EntityTypeProxy(val entityType: EntityType) : Proxy

	open val proxy: EntityTypeProxy by ProxyProvider<EntityTypeProxy>(
			"blue.sparse.minecraft.client.entity.proxy.Default",
			"blue.sparse.minecraft.server.entity.proxy.Default",
			this
	)

	init {
        register(this)
	}

	constructor(id: String) : this(Identifier(id))

	open fun update(entity: Entity<*>, delta: Float) {}

	open fun createData(): EntityData {
		return EntityData.Default
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