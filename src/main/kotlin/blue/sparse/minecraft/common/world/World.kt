package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.ints.Vector3i
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class World(val name: String, val id: UUID = UUID.randomUUID()) {

	private val regions = ConcurrentHashMap<Vector3i, Region>()
	private val key = ThreadLocal.withInitial { Vector3i(0) }

	fun getRegion(x: Int, y: Int, z: Int): Region {
		val key = this.key.get()
		key.assign(x, y, z)

		return regions.getOrPut(key) { Region(this, key.clone()) }
	}

	fun getChunk(x: Int, y: Int, z: Int): Chunk? {
		val worldRegionX = worldChunkToWorldRegion(x)
		val worldRegionY = worldChunkToWorldRegion(y)
		val worldRegionZ = worldChunkToWorldRegion(z)

		val region = getRegion(x, y, z)
		val regionChunkX = worldChunkToRegionChunk(x)
		val regionChunkY = worldChunkToRegionChunk(y)
		val regionChunkZ = worldChunkToRegionChunk(z)

		return region.getChunk(regionChunkX, regionChunkY, regionChunkZ)
	}

	fun getOrGenerateChunk(x: Int, y: Int, z: Int): Chunk {
		val worldRegionX = worldChunkToWorldRegion(x)
		val worldRegionY = worldChunkToWorldRegion(y)
		val worldRegionZ = worldChunkToWorldRegion(z)

		val region = getRegion(x, y, z)
		val regionChunkX = worldChunkToRegionChunk(x)
		val regionChunkY = worldChunkToRegionChunk(y)
		val regionChunkZ = worldChunkToRegionChunk(z)

		return region.getOrGenerateChunk(regionChunkX, regionChunkY, regionChunkZ)
	}

	fun getBlock(x: Int, y: Int, z: Int): BlockView? {
		val worldChunkX = worldBlockToWorldChunk(x)
		val worldChunkY = worldBlockToWorldChunk(y)
		val worldChunkZ = worldBlockToWorldChunk(z)

		val chunk = getChunk(worldChunkX, worldChunkY, worldChunkZ) ?: return null
		val chunkBlockX = worldBlockToChunkBlock(x)
		val chunkBlockY = worldBlockToChunkBlock(y)
		val chunkBlockZ = worldBlockToChunkBlock(z)

		return chunk[chunkBlockX, chunkBlockY, chunkBlockZ]
	}

	fun getOrGenerateBlock(x: Int, y: Int, z: Int): BlockView {
		val worldChunkX = worldBlockToWorldChunk(x)
		val worldChunkY = worldBlockToWorldChunk(y)
		val worldChunkZ = worldBlockToWorldChunk(z)

		val chunk = getOrGenerateChunk(worldChunkX, worldChunkY, worldChunkZ)
		val chunkBlockX = worldBlockToChunkBlock(x)
		val chunkBlockY = worldBlockToChunkBlock(y)
		val chunkBlockZ = worldBlockToChunkBlock(z)

		return chunk[chunkBlockX, chunkBlockY, chunkBlockZ]
	}

	operator fun get(x: Int, y: Int, z: Int): BlockView? {
		return getBlock(x, y, z)
	}

	companion object {
		internal fun worldChunkToRegionChunk(i: Int): Int {
			return i and Region.MASK
		}

		internal fun worldChunkToWorldRegion(i: Int): Int {
			return i shr Region.BITS
		}

		internal fun worldBlockToChunkBlock(i: Int): Int {
			return i and Chunk.MASK
		}

		internal fun worldBlockToWorldChunk(i: Int): Int {
			return i shr Chunk.BITS
		}
	}
}