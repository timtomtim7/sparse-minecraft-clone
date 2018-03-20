package blue.sparse.minecraft.common.entity

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

	open fun onTick(entity: Entity<*>) {
		
	}

	companion object {
		internal val registry = LinkedHashMap<Identifier, EntityType>()

		private fun register(type: EntityType) {
			if (type.identifier in registry)
				throw IllegalArgumentException("Entity with identifier \"${type.identifier}\" is already registered.")

			registry[type.identifier] = type
		}


	}
}