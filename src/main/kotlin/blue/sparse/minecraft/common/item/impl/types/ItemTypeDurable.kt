package blue.sparse.minecraft.common.item.impl.types

import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.Identifier

abstract class ItemTypeDurable(identifier: Identifier, val maxDurability: Int) : ItemType(identifier, 1) {
    constructor(identifier: String, maxDurability: Int) : this(Identifier(identifier), maxDurability)
}