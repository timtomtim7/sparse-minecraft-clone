package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.util.random
import org.lwjgl.stb.STBPerlin
import java.util.concurrent.ConcurrentHashMap

class Region(val world: World, position: Vector3i) {

	private val chunks = ConcurrentHashMap<Vector3i, Chunk>()
	private val key = ThreadLocal.withInitial { Vector3i(0) }

	val worldRegionPosition: Vector3i = position
		get() = field.clone()

	val loadedChunks: Collection<Chunk>
		get() = chunks.values

	fun getChunk(x: Int, y: Int, z: Int): Chunk? {
		boundsCheck(x, y, z)

		val key = this.key.get()
		key.assign(x, y, z)
		return chunks[key]
	}

	private val ores = arrayOf(
			BlockType.coalOre,
			BlockType.ironOre,
			BlockType.goldOre,
			BlockType.lapisOre,
			BlockType.redstoneOre,
			BlockType.diamondOre,
			BlockType.emeraldOre
	)

	fun getOrGenerateChunk(x: Int, y: Int, z: Int): Chunk {
		boundsCheck(x, y, z)

		val key = this.key.get()
		key.assign(x, y, z)
		var chunk = chunks[key]
		if(chunk != null)
			return chunk

		chunk = Chunk(this, key.clone())
		chunks[chunk.regionChunkPosition] = chunk
		//TODO: Invoke world generator on chunk
		val pos = chunk.worldChunkPosition
		if(pos.y < 0) {
			chunk.fill(BlockType.stone)

			val random = random

			for(bx in 0 until Chunk.SIZE) {
				for (by in 0 until Chunk.SIZE) {
					for (bz in 0 until Chunk.SIZE) {
						if(random.nextDouble() < 0.9)
							continue

						chunk.setBlockType(bx, by, bz, ores.random())
					}
				}
			}

		} else if(pos.y == 0) {
			val wb = chunk.worldBlockPosition

			for(bx in 0 until Chunk.SIZE) {
				for (bz in 0 until Chunk.SIZE) {
					val rx = wb.x + bx
					val rz = wb.z + bz

					val maxY = (STBPerlin.stb_perlin_noise3(rx * 0.05f, 0f, rz * 0.05f, 1024, 1024, 1024) * 8 + 8).toInt()

					for (by in 0..maxY) {
						chunk.setBlockType(bx, by, bz, BlockType.dirt)
					}
				}
			}
		} else{
			chunk.fill(null)
		}
		return chunk
	}

	companion object {
		const val BITS = 4
		const val SIZE = 1 shl BITS
		const val MASK = SIZE - 1

		private fun boundsCheck(x: Int, y: Int, z: Int) {
			if(x < 0 || y < 0 || z < 0 || x >= SIZE || y >= SIZE || z >= SIZE)
				throw IllegalArgumentException("Chunk coordinates out of range ($SIZE): $x, $y, $z")
		}

//		internal fun regionChunkToWorldChunk(r: Int, i: Int): Int {
//			return (r shl BITS) or i
//		}
//
//		internal fun worldChunkToWorldRegion(i: Int): Int {
//			return i shr Region.BITS
//		}
//
//		internal fun worldBlockToChunkBlock(i: Int): Int {
//			return i and Chunk.MASK
//		}
//
//		internal fun worldBlockToWorldChunk(i: Int): Int {
//			return i shr Chunk.BITS
//		}
	}
}