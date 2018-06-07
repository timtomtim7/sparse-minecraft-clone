package blue.sparse.minecraft.common.item.types.armor

import blue.sparse.minecraft.common.util.Identifier

abstract class ItemTypeBoots(identifier: Identifier, maxDurability: Int) : ItemTypeArmor(identifier, maxDurability) {
    constructor(identifier: String, maxDurability: Int) : this(Identifier(identifier), maxDurability)
}