package blue.sparse.minecraft.common.events

import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.event.Event

sealed class GameInitializationEvent(val side: Minecraft.Side): Event {
	class Client: GameInitializationEvent(Minecraft.Side.CLIENT)
	class Server: GameInitializationEvent(Minecraft.Side.SERVER)
}