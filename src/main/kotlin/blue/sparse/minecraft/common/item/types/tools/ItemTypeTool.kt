package blue.sparse.minecraft.common.item.types.tools

import blue.sparse.minecraft.common.item.types.ItemTypeDurable
import blue.sparse.minecraft.common.util.Identifier

abstract class ItemTypeTool(identifier: Identifier, maxDurability: Int) : ItemTypeDurable(identifier, maxDurability) {
    constructor(identifier: String, maxDurability: Int) : this(Identifier(identifier), maxDurability)
}