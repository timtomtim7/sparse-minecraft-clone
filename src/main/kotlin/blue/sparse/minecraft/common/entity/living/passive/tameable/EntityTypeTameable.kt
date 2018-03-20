package blue.sparse.minecraft.common.entity.living.passive.tameable

import blue.sparse.minecraft.common.entity.living.passive.EntityTypePassive
import blue.sparse.minecraft.common.util.Identifier

abstract class EntityTypeTameable(id: Identifier) : EntityTypePassive(id) {
    constructor(id: String) : this(Identifier(id))
}