package blue.sparse.minecraft.common.entity.living.hostile.boss

import blue.sparse.minecraft.common.entity.living.hostile.EntityTypeHostile
import blue.sparse.minecraft.common.util.Identifier

abstract class EntityTypeBoss(id: Identifier) : EntityTypeHostile(id) {
    constructor(id: String) : this(Identifier(id))
}