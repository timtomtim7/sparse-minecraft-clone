package blue.sparse.minecraft.common.item.impl.types

import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.util.Identifier

abstract class ItemTypeBoat(identifier: Identifier) : ItemType(identifier, 1) {
    constructor(identifier: String) : this(Identifier(identifier))
}