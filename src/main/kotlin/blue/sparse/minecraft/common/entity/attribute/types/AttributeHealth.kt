package blue.sparse.minecraft.common.entity.attribute.types

import blue.sparse.math.clamp
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.attribute.EntityAttribute
import blue.sparse.minecraft.common.entity.attribute.EntityAttributeType

class AttributeHealth(val entity: Entity<*>, value: Float) : EntityAttribute<Float>(Companion, value) {

	override var value = value
		set(value) {
			field = clamp(value, 0f, entity[AttributeMaxHealth])
		}

	companion object : EntityAttributeType<Float, AttributeHealth>("generic.health") {
		override fun create(entity: Entity<*>): AttributeHealth {
			return AttributeHealth(entity, if(entity has AttributeMaxHealth) entity[AttributeMaxHealth] else 0f)
		}
	}

}