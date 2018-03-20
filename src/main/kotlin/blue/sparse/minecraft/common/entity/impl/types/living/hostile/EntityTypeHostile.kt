package blue.sparse.minecraft.common.entity.impl.types.living.hostile

import blue.sparse.minecraft.common.entity.impl.types.living.EntityTypeLiving
import blue.sparse.minecraft.common.util.Identifier

abstract class EntityTypeHostile(id: Identifier) : EntityTypeLiving(id) {
    constructor(id: String) : this(Identifier(id))
}