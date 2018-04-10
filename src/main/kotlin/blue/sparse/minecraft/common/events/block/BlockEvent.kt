package blue.sparse.minecraft.common.events.block

import blue.sparse.minecraft.common.event.Event
import blue.sparse.minecraft.common.world.BlockView

interface BlockEvent: Event {
	val block: BlockView
}