package blue.sparse.minecraft.common.player.gamemode

import blue.sparse.minecraft.common.util.Identifier

abstract class GameMode(val id: Identifier) {
    constructor(id: String) : this(Identifier(id))
}