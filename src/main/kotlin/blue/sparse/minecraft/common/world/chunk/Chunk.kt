package blue.sparse.minecraft.common.world.chunk

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.biome.BiomeType
import blue.sparse.minecraft.common.block.Block
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.util.math.AABB
import blue.sparse.minecraft.common.util.math.BlockFace
import blue.sparse.minecraft.common.util.proxy.Proxy
import blue.sparse.minecraft.common.util.proxy.ProxyProvider
import blue.sparse.minecraft.common.world.*
import java.util.concurrent.LinkedTransferQueue
import kotlin.math.max

class Chunk constructor(
		val region: Region,
		position: Vector3i,
		private val blocks: ChunkElementStorage<Block>,
		private val biomes: ChunkElementStorage<BiomeType>
) {

	private val light = LightStorage()

	val regionChunkPosition: Vector3i = position
		get() = field.clone()

	val worldChunkPosition = run {
		val wr = region.worldRegionPosition
		val rc = regionChunkPosition
		Vector3i(regionChunkToWorldChunk(wr.x, rc.x), regionChunkToWorldChunk(wr.y, rc.y), regionChunkToWorldChunk(wr.z, rc.z))
	}
		get() = field.clone()

	val worldBlockPosition = run {
		val wr = region.worldRegionPosition
		val rc = regionChunkPosition
		Vector3i(chunkBlockToWorldBlock(wr.x, rc.x, 0), chunkBlockToWorldBlock(wr.y, rc.y, 0), chunkBlockToWorldBlock(wr.z, rc.z, 0))
	}
		get() = field.clone()

	val world: World
		get() = region.world

	//block type		12 bits 0xFFF
	//block state		 4 bits 0xF
	//block light 		 4 bits 0xF
	//sun light			 4 bits 0xF
	//biome				 8 bits 0xFF
	//-------------------------
	//					32 bits

	val proxy by ProxyProvider.invoke<ChunkProxy>(
			"blue.sparse.minecraft.client.world.proxy.ClientChunkProxy",
			"blue.sparse.minecraft.server.world.proxy.ServerChunkProxy",
			this
	)

//	fun getBlock(index: Int) = blocks[index]
//
//	fun getBiome(index: Int) = biomes[index]
//
//	fun getBlockLightFloat(index: Int) = light.getRGBFloatVector(xFromIndex(index), yFromIndex(index), zFromIndex(index))
//
//	fun getBlockLight(index: Int) = light.getRGBIntVector(xFromIndex(index), yFromIndex(index), zFromIndex(index))
//
//	fun getSunLightFloat(index: Int) = light.getSunFloat(xFromIndex(index), yFromIndex(index), zFromIndex(index))
//
//	fun getSunLight(index: Int) = light.getSun(xFromIndex(index), yFromIndex(index), zFromIndex(index))


	fun getBlock(x: Int, y: Int, z: Int) = blocks[x, y, z]

	fun getBiome(x: Int, y: Int, z: Int) = biomes[x, y, z]

	fun getBlockLightFloat(x: Int, y: Int, z: Int) = light.getRGBFloatVector(x, y, z)

	fun getBlockLight(x: Int, y: Int, z: Int) = light.getRGBIntVector(x, y, z)

	fun getSunLightFloat(x: Int, y: Int, z: Int) = light.getSunFloat(x, y, z)

	fun getSunLight(x: Int, y: Int, z: Int) = light.getSun(x, y, z)


//	fun setBlock(index: Int, value: Block) {
//		blocks[index] = value
//		proxy.changed(xFromIndex(index), yFromIndex(index), zFromIndex(index))
//	}
//
//	fun setBiome(index: Int, value: BiomeType?) {
//		biomes[index] = value ?: BiomeType.void
//		proxy.changed(xFromIndex(index), yFromIndex(index), zFromIndex(index))
//	}


	fun setBlock(x: Int, y: Int, z: Int, value: Block) {
		val light = value.type?.lightEmission
		if (light != null) {
			propagateBlockLight(light, Vector3i(x, y, z), emptySet())
		}
		blocks[x, y, z] = value
		proxy.changed(x, y, z)
	}

	fun setBiome(x: Int, y: Int, z: Int, value: BiomeType?) {
		biomes[x, y, z] = value ?: BiomeType.void
//		proxy.changed(x, y, z)
	}

	fun setBlockLight(x: Int, y: Int, z: Int, value: Vector3i) {
		light.setRGBIntVector(x, y, z, value)
//		proxy.changed(x, y, z)
	}

	fun setSunLight(x: Int, y: Int, z: Int, value: Int) {
		light.setSun(x, y, z, value)
//		proxy.changed(x, y, z)
	}

	/*
		Other
	 */

	fun isEmpty(x: Int, y: Int, z: Int): Boolean {
		return blocks[x, y, z].type == null
	}

	fun isNotEmpty(x: Int, y: Int, z: Int): Boolean {
		return blocks[x, y, z].type != null
	}

//	operator fun get(index: Int): BlockView {
//		return get(xFromIndex(index), yFromIndex(index), zFromIndex(index))
//	}

	operator fun get(chunkBlock: Vector3i): BlockView {
		return get(chunkBlock.x, chunkBlock.y, chunkBlock.z)
	}

	operator fun get(x: Int, y: Int, z: Int): BlockView {
		return BlockView(this, x, y, z)
	}


	private fun propagateBlockLight(light: Vector3i, origin: Vector3i, chunkBlacklist: Set<Chunk>) {
		if(getBlock(origin.x, origin.y, origin.z).isOccluding)
			return

		setBlockLight(origin.x, origin.y, origin.z, light)
		val queue = LinkedTransferQueue<Vector3i>()
		queue.add(origin)

		val affectedChunks = HashSet<Chunk>()

		while (queue.isNotEmpty()) {
			val current = queue.poll() ?: break

			val currentLight = getBlockLight(current.x, current.y, current.z)

			for (direction in BlockFace.values()) {
				val position = current + direction.offset

				if (safeBoundsCheck(position.x, position.y, position.z)) {
					val worldBlock = worldBlockPosition + position

					val adjacentChunk = world.getBlock(worldBlock)?.chunk ?: continue
					if(adjacentChunk in chunkBlacklist) continue
					val newBlacklist = chunkBlacklist + this

					val newLight = Vector3i(
							max(currentLight.x - 1, 0),
							max(currentLight.y - 1, 0),
							max(currentLight.z - 1, 0)
					)

					val adjChunkRelative = worldBlockToChunkBlock(worldBlock)
					adjacentChunk.propagateBlockLight(newLight, adjChunkRelative, newBlacklist)
					affectedChunks.add(adjacentChunk)
//					adjacentChunk.proxy.changed(adjChunkRelative.x, adjChunkRelative.y, adjChunkRelative.z)

					continue
				}

				if(getBlock(position.x, position.y, position.z).isOccluding) {
//					println("Block is occluding $position")
					continue
				}

				val adjLight = getBlockLight(position.x, position.y, position.z)

				val newLight = Vector3i(
						max(adjLight.x, max(currentLight.x - 1, 0)),
						max(adjLight.y, max(currentLight.y - 1, 0)),
						max(adjLight.z, max(currentLight.z - 1, 0))
				)

				if(adjLight != newLight) {
//					println("$adjLight -> $newLight")

					setBlockLight(position.x, position.y, position.z, newLight)
					queue.add(position)
				}

			}
		}

		affectedChunks.forEach { it.proxy.changed(SIZE / 2, SIZE / 2, SIZE / 2) }
	}

//	fun setAll(x: Int, y: Int, z: Int, type: BlockType? = getType(x, y, z), state: BlockState = BlockState.Default) {
//
//	}

	operator fun contains(entity: Entity<*>): Boolean {
		val bounds = AABB(Vector3f(0f), Vector3f(SIZE.toFloat()))
		val pos = worldBlockPosition.toFloatVector()

		val entityBounds = entity.type.bounds

		return bounds.isIntersecting(pos, entityBounds, entity.position)
	}

	internal fun debugBoundingBox(color: Vector3f = Vector3f(1f)) {
		bounds.debugRender(worldBlockPosition.toFloatVector(), color)
	}

	internal fun unloaded() {
		proxy.unloaded()
	}

	abstract class ChunkProxy(val chunk: Chunk) : Proxy {
		abstract fun changed(x: Int, y: Int, z: Int)

		abstract fun unloaded()
	}

	companion object {
		const val BITS = 5
		const val SIZE = 1 shl BITS
		const val MASK = SIZE - 1
		const val VOLUME = SIZE * SIZE * SIZE

		val bounds = AABB(Vector3f(0f), Vector3f(SIZE.toFloat()))

		fun indexOfBlock(x: Int, y: Int, z: Int): Int {
			if (x < 0 || x >= SIZE || y < 0 || y >= SIZE || z < 0 || z >= SIZE) return -1
			return x + (y * SIZE) + (z * SIZE * SIZE)
		}

		fun xFromIndex(index: Int) = index % SIZE
		fun yFromIndex(index: Int) = (index / SIZE) % SIZE
		fun zFromIndex(index: Int) = ((index / SIZE) / SIZE) % SIZE

		private fun safeBoundsCheck(x: Int, y: Int, z: Int): Boolean {
			return x < 0 || y < 0 || z < 0 || x >= SIZE || y >= SIZE || z >= SIZE
		}
	}
}