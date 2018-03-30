package blue.sparse.minecraft.common.entity.impl.types.living

import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.entity.attribute.EntityAttributeType
import blue.sparse.minecraft.common.entity.attribute.types.AttributeHealth
import blue.sparse.minecraft.common.entity.attribute.types.AttributeMaxHealth
import blue.sparse.minecraft.common.util.Identifier

abstract class EntityTypeLiving(id: Identifier) : EntityType(id) {
	constructor(id: String) : this(Identifier(id))

	//    Default for player, please change accordingly
	open val eyeHeight: Float = 1.62f

	open val maxHealth: Float = 20f

	override fun getAttributeTypes(): List<EntityAttributeType<*, *>> {
		return listOf(AttributeMaxHealth, AttributeHealth)
	}
}