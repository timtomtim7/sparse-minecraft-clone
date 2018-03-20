package blue.sparse.minecraft.common.entity.impl.types.living.hostile.slime

import blue.sparse.minecraft.common.entity.impl.types.living.hostile.EntityTypeHostile
import blue.sparse.minecraft.common.util.Identifier

abstract class AbstractSlime(id: Identifier) : EntityTypeHostile(id) {
    constructor(id: String) : this(Identifier(id))
}