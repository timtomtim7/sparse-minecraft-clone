package blue.sparse.minecraft.common.item.impl.types.tools

import blue.sparse.minecraft.common.util.Identifier

abstract class ItemTypeWeapon(identifier: Identifier, maxDurability: Int) : ItemTypeTool(identifier, maxDurability) {
    constructor(identifier: String, maxDurability: Int) : this(Identifier(identifier), maxDurability)
}