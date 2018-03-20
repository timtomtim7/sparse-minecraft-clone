package blue.sparse.minecraft.common.entity.living.passive.cow

import blue.sparse.minecraft.common.entity.living.passive.EntityTypePassive
import blue.sparse.minecraft.common.util.Identifier

abstract class AbstractCow(id: Identifier) : EntityTypePassive(id) {
    constructor(id: String) : this(Identifier(id))
}