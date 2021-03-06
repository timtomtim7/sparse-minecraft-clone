package blue.sparse.minecraft.common.item.types.armor

import blue.sparse.minecraft.common.item.types.ItemTypeDurable
import blue.sparse.minecraft.common.util.Identifier

abstract class ItemTypeArmor(identifier: Identifier, maxDurability: Int) : ItemTypeDurable(identifier, maxDurability) {
    constructor(identifier: String, maxDurability: Int) : this(Identifier(identifier), maxDurability)
}