package blue.sparse.minecraft.common.event

import blue.sparse.minecraft.common.Minecraft

interface Event

fun <T: Event> T.post(bus: EventBus = Minecraft.events): T {
	bus.post(this)
	return this
}