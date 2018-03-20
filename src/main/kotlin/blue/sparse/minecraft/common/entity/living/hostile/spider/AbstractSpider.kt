package blue.sparse.minecraft.common.entity.living.hostile.spider

import blue.sparse.minecraft.common.entity.living.hostile.EntityTypeHostile
import blue.sparse.minecraft.common.util.Identifier

abstract class AbstractSpider(id: Identifier) : EntityTypeHostile(id) {
    constructor(id: String) : this(Identifier(id))
}