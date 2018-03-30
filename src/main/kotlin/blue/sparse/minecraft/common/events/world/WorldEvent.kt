package blue.sparse.minecraft.common.events.world

import blue.sparse.minecraft.common.event.Event
import blue.sparse.minecraft.common.world.World

interface WorldEvent: Event {
	val world: World
}