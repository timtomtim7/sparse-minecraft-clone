package blue.sparse.minecraft.common.block.types

import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.util.Identifier

abstract class BlockTypeGravity(identifier: Identifier) : BlockType(identifier) {
    constructor(identifier: String) : this(Identifier(identifier))
}