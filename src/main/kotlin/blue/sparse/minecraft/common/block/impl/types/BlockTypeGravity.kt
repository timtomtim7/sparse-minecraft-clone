package blue.sparse.minecraft.common.block.impl.types

import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.util.Identifier

abstract class BlockTypeGravity(identifier: Identifier) : BlockType(identifier) {
    constructor(identifier: String) : this(Identifier(identifier))
}