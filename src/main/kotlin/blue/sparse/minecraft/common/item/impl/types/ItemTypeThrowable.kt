package blue.sparse.minecraft.common.item.impl.types

import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.Identifier

abstract class ItemTypeThrowable(identifier: Identifier, maxStackSize: Int = 16) : ItemType(identifier, maxStackSize) {
    constructor(identifier: String, maxStackSize: Int = 16) : this(Identifier(identifier), maxStackSize)
}