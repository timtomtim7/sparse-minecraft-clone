package blue.sparse.minecraft.common.item.impl.types.armor

import blue.sparse.minecraft.common.util.Identifier

abstract class ItemTypeLeggings(identifier: Identifier, maxDurability: Int) : ItemTypeArmor(identifier, maxDurability) {
    constructor(identifier: String, maxDurability: Int) : this(Identifier(identifier), maxDurability)
}