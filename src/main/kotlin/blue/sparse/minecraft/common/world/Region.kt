package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.world.generator.ChunkGenerator
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

		val blocks = ChunkGenerator.blocks.get()
		blocks.fill(null)
		val worldRegion = worldRegionPosition
		val position = Vector3i(
				regionChunkToWorldChunk(worldRegion.x, x),
				regionChunkToWorldChunk(worldRegion.y, y),
				regionChunkToWorldChunk(worldRegion.z, z)
		)
		world.generator.generate(position, blocks)

		val data = IntArray(Chunk.VOLUME) { blocks[it]?.rawID ?: 0 }
		chunk = Chunk(this, key.clone(), data)
		chunks[chunk.regionChunkPosition] = chunk

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
	}
}