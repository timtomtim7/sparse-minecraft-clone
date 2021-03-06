package blue.sparse.minecraft.common.entity.impl.types.living.hostile.undead.zombie

import blue.sparse.minecraft.common.entity.impl.types.living.hostile.undead.EntityTypeUndead
import blue.sparse.minecraft.common.util.Identifier

abstract class AbstractZombie(id: Identifier) : EntityTypeUndead(id) {
    constructor(id: String) : this(Identifier(id))
}