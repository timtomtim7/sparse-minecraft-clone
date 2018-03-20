package blue.sparse.minecraft.common.entity.living

import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.util.Identifier

abstract class EntityTypeLiving(id: Identifier) : EntityType(id) {
    constructor(id: String) : this(Identifier(id))
}