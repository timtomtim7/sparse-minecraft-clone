package blue.sparse.minecraft.common.entity.impl.types.living.passive.tameable

import blue.sparse.minecraft.common.entity.impl.types.living.passive.EntityTypePassive
import blue.sparse.minecraft.common.util.Identifier

abstract class EntityTypeTameable(id: Identifier) : EntityTypePassive(id) {
    constructor(id: String) : this(Identifier(id))
}