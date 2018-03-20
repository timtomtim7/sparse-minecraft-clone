package blue.sparse.minecraft.common.entity.living.hostile

import blue.sparse.minecraft.common.entity.living.EntityTypeLiving
import blue.sparse.minecraft.common.util.Identifier

abstract class EntityTypeHostile(id: Identifier) : EntityTypeLiving(id) {
    constructor(id: String) : this(Identifier(id))
}