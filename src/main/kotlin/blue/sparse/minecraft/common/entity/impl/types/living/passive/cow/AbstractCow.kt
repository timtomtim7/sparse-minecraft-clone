package blue.sparse.minecraft.common.entity.impl.types.living.passive.cow

import blue.sparse.minecraft.common.entity.impl.types.living.passive.EntityTypePassive
import blue.sparse.minecraft.common.util.Identifier

abstract class AbstractCow(id: Identifier) : EntityTypePassive(id) {
    constructor(id: String) : this(Identifier(id))
}