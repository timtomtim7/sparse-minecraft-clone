package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.biome.BiomeType
import blue.sparse.minecraft.common.block.Block
import blue.sparse.minecraft.common.util.getValue
import blue.sparse.minecraft.common.util.threadLocal
import blue.sparse.minecraft.common.world.chunk.Chunk
import blue.sparse.minecraft.common.world.chunk.ChunkElementStorage
import java.util.concurrent.ConcurrentHashMap

class Region(val world: World, position: Vector3i) {

	private val chunks = ConcurrentHashMap<Vector3i, Chunk>()
//	private val key = ThreadLocal.withInitial { Vector3i(0) }
	private val key by threadLocal { Vector3i(0)}

	var lastAccessTime = System.currentTimeMillis()
		private set

	val worldRegionPosition: Vector3i = position
		get() = field.clone()

	val loadedChunks: Collection<Chunk>
		get() = chunks.values

	fun getChunk(pos: Vector3i): Chunk? {
		boundsCheck(pos.x, pos.y, pos.z)
		accessed()
		return chunks[pos]
	}

	fun getChunk(x: Int, y: Int, z: Int): Chunk? {
		boundsCheck(x, y, z)
		accessed()
		return chunks[key.apply { assign(x, y, z) }]
	}

	fun getOrGenerateChunk(x: Int, y: Int, z: Int): Chunk {
		return getOrGenerateChunk(x, y, z)
	}

	fun getOrGenerateChunk(regionChunk: Vector3i): Chunk {
		boundsCheck(regionChunk.x, regionChunk.y, regionChunk.z)
		accessed()

		var chunk = chunks[regionChunk]
		if (chunk != null)
			return chunk

		val blocks = ChunkElementStorage(Block.empty)
		val biomes = ChunkElementStorage<BiomeType>(BiomeType.void)

		val worldRegion = worldRegionPosition
		val position = regionChunkToWorldChunk(worldRegion, regionChunk)
		world.generator.generate(position, blocks, biomes)

		chunk = Chunk(this, regionChunk.clone(), blocks, biomes)
		chunks[chunk.regionChunkPosition] = chunk

		return chunk
	}

	fun accessed() {
		lastAccessTime = System.currentTimeMillis()
	}

	internal fun unloaded() {
		loadedChunks.forEach(Chunk::unloaded)

		//TODO: Save region?
	}

	companion object {
		const val BITS = 4
		const val SIZE = 1 shl BITS
		const val MASK = SIZE - 1

		private fun boundsCheck(x: Int, y: Int, z: Int) {
			if (x < 0 || y < 0 || z < 0 || x >= SIZE || y >= SIZE || z >= SIZE)
				throw IllegalArgumentException("Chunk coordinates out of range ($SIZE): $x, $y, $z")
		}
	}
}