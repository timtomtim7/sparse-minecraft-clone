package blue.sparse.minecraft.common.events.block

import blue.sparse.minecraft.common.events.world.WorldEvent
import blue.sparse.minecraft.common.world.BlockView
import blue.sparse.minecraft.common.world.World

class BlockChangedEvent(override val world: World, override val block: BlockView): BlockEvent, WorldEvent