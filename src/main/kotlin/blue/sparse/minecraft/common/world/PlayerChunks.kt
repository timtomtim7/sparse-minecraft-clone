package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.floats.lengthSquared
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.math.vectors.ints.abs
import blue.sparse.minecraft.common.player.Player

class PlayerChunks(val player: Player, horizontalDistance: Int = 8, verticalDistance: Int = 8) : Iterable<Vector3i> {

	private lateinit var order: Array<Vector3i>

	var horizontalDistance = horizontalDistance
		set(value) {
			if (field != value) {
				field = value
				generateOrder()
			}
		}

	var verticalDistance = verticalDistance
		set(value) {
			if (field != value) {
				field = value
				generateOrder()
			}
		}

	val chunkPosition: Vector3i?
		get() = player.entity?.block?.chunk?.worldChunkPosition

	val volume: Int
		get() = (horizontalDistance * 2 + 1) * (horizontalDistance * 2 + 1) * (verticalDistance * 2 + 1)

	init {
		generateOrder()
	}

	override fun iterator(): Iterator<Vector3i> {
		val chunkPosition = chunkPosition ?: return emptyList<Vector3i>().iterator()

		return order.asSequence().map { chunkPosition + it }.iterator()
	}

	operator fun contains(chunk: Vector3i): Boolean {
		val chunkPosition = chunkPosition ?: return false
		val delta = abs(chunk - chunkPosition)

		return delta.x < horizontalDistance && delta.z < horizontalDistance && delta.y < horizontalDistance
	}

	operator fun contains(chunk: Chunk) = chunk.worldChunkPosition in this

	private fun generateOrder() {

		order = Array(volume) { Vector3i(0) }

		var i = 0
		for (x in -horizontalDistance..horizontalDistance) {
			for (z in -horizontalDistance..horizontalDistance) {
				for (y in -verticalDistance..verticalDistance) {
					order[i].assign(x, y, z)

					i++
				}
			}
		}

		order.sortBy { lengthSquared(it.toFloatVector()) }

	}
}