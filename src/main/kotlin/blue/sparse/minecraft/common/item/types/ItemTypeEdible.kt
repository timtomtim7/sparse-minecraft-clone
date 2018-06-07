package blue.sparse.minecraft.common.item.types

import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.Identifier

abstract class ItemTypeEdible(identifier: Identifier, maxStackSize: Int = 64) : ItemType(identifier, maxStackSize) {
    constructor(identifier: String, maxStackSize: Int = 64) : this(Identifier(identifier), maxStackSize)
}