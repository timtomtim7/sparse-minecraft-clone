package blue.sparse.minecraft.common.entity.attribute.types

import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.attribute.EntityAttribute
import blue.sparse.minecraft.common.entity.attribute.EntityAttributeType
import blue.sparse.minecraft.common.entity.impl.types.living.EntityTypeLiving

class AttributeMaxHealth(value: Float) : EntityAttribute<Float>(Companion, value) {

	companion object : EntityAttributeType<Float, AttributeMaxHealth>("generic.maxHealth") {
		override fun create(entity: Entity<*>): AttributeMaxHealth {
			return AttributeMaxHealth((entity.type as? EntityTypeLiving)?.maxHealth ?: 0.0f)
		}
	}

}