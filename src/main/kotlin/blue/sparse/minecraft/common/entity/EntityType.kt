package blue.sparse.minecraft.common.entity

import blue.sparse.minecraft.common.util.*

abstract class EntityType(val identifier: Identifier) {
	open val proxy: EntityTypeProxy by ProxyProvider<EntityTypeProxy>(
			"blue.sparse.minecraft.client.entity.proxy.Default",
			"blue.sparse.minecraft.server.entity.proxy.Default",
			this
	)

	constructor(id: String) : this(Identifier(id))

	init {
		register(this)
	}

	abstract class EntityTypeProxy(val entityType: EntityType) : Proxy

	companion object {
		internal val registry = LinkedHashMap<Identifier, EntityType>()

		private fun register(type: EntityType) {
			if (type.identifier in registry)
				throw IllegalArgumentException("Entity with identifier \"${type.identifier}\" is already registered.")

			registry[type.identifier] = type
		}


	}
}