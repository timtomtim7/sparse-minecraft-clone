package blue.sparse.minecraft.client.world.proxy

import blue.sparse.minecraft.client.sky.OverworldSky
import blue.sparse.minecraft.common.world.World

class ClientWorldProxy(world: World) : World.WorldProxy(world) {
	val sky = OverworldSky()
}