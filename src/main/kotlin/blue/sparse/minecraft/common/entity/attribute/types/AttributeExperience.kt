package blue.sparse.minecraft.common.entity.attribute.types

import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.attribute.EntityAttribute
import blue.sparse.minecraft.common.entity.attribute.EntityAttributeType
import kotlin.math.max

class AttributeExperience(val entity: Entity<*>, value: Long) : EntityAttribute<Long>(Companion, value) {

	override var value = value
		set(value) {
			field = max(value, 0L)
		}

	companion object : EntityAttributeType<Long, AttributeExperience>("generic.experience") {
		override fun create(entity: Entity<*>): AttributeExperience {
			return AttributeExperience(entity, 0L)
		}
	}

}