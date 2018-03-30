package blue.sparse.minecraft.common.entity.attribute

abstract class EntityAttribute<V : Any>(
		val attributeType: EntityAttributeType<V, out EntityAttribute<V>>,
		open var value: V
)