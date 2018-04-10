package blue.sparse.minecraft.client.world.proxy

import blue.sparse.math.vectors.floats.*
import blue.sparse.minecraft.client.sky.overworld.OverworldSky
import blue.sparse.minecraft.client.world.render.WorldRenderer
import blue.sparse.minecraft.common.world.World

class ClientWorldProxy(world: World) : World.WorldProxy(world) {
	val sky = OverworldSky()
	val renderer = WorldRenderer(world)

	val lightDirection: Vector3f
		get() {
			return sky.sun.direction * Quaternion4f(Vector3f(0f, 1f, 0f), Math.toRadians(45.0).toFloat())
//			var dir = sky.sun.direction
//			val y = dir.y
//			dir.y = 0f
//			dir.z = dir.x
//			dir = normalize(dir) * (2f / 3f)
//			dir.y = y
//
//			return dir
//			return normalize(sky.sun.direction + Vector3f(0f, 0f, 1f))
		}
}