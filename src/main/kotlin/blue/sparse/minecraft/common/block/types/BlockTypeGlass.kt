package blue.sparse.minecraft.common.block.types

import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.util.Identifier

abstract class BlockTypeGlass(identifier: Identifier) : BlockType(identifier) {
    override val transparent = true

    constructor(identifier: String) : this(Identifier(identifier))
}