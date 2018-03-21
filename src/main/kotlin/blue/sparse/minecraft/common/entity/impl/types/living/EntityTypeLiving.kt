package blue.sparse.minecraft.common.entity.impl.types.living

import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.util.Identifier

abstract class EntityTypeLiving(id: Identifier) : EntityType(id) {
	constructor(id: String) : this(Identifier(id))

	//    Default for player, please change accordingly
	open val eyeHeight: Float = 1.62f
}