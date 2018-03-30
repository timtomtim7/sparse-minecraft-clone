package blue.sparse.minecraft.common.entity.attribute

import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.util.Identifier

abstract class EntityAttributeType<V : Any, A : EntityAttribute<V>>(/*val type: KClass<T>, */val identifier: Identifier) {

	constructor(/*type: KClass<T>, */identifier: String) : this(/*type, */Identifier(identifier))

	abstract fun create(entity: Entity<*>): A

	open fun transform(attribute: EntityAttribute<V>, value: V): V {
		return value
	}

}