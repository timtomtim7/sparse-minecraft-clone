package blue.sparse.minecraft.common.entity.impl.types.living.passive.tameable.horse

import blue.sparse.minecraft.common.entity.impl.types.living.passive.tameable.EntityTypeTameable
import blue.sparse.minecraft.common.util.Identifier

abstract class AbstractHorse(id: Identifier) : EntityTypeTameable(id) {
    constructor(id: String) : this(Identifier(id))
}