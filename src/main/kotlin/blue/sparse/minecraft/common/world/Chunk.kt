package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.client.player.ClientPlayer
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.util.*

class Chunk(val region: Region, position: Vector3i) {

	private var data: IntArray? = null// = IntArray(SIZE * SIZE * SIZE)
	private var filled: Int = 0

	var lastChangedMillis: Long = System.currentTimeMillis()
		private set

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
		Vector3i(chunkBlockToWorldBlock(wr.x, rc.x, 0), chunkBlockToWorldBlock(wr.y, rc.y, 0),chunkBlockToWorldBlock(wr.z, rc.z, 0))
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

	fun fill(type: BlockType?) {
		data = null
		filled = type?.id ?: 0
	}

	internal fun getBlockID(index: Int): Int {
		return (data?.get(index) ?: filled) and 0xFFF
	}

	internal fun getBlockID(x: Int, y: Int, z: Int): Int {
		return getBlockID(indexOfBlock(x, y, z))
	}

	fun getBlockType(index: Int): BlockType? {
		return BlockType[getBlockID(index)]
	}

	fun getBlockType(x: Int, y: Int, z: Int): BlockType? {
		return BlockType[getBlockID(x, y, z)]
	}

	fun setBlockType(x: Int, y: Int, z: Int, type: BlockType?) {
		var data = getRaw(x, y, z)
		data = (data.inv() or 0xFFF).inv() or (type?.id ?: 0)
		setRaw(x, y, z, data)
//		data = (data or 0xFFF).inv() or (type?.id ?: 0)
	}

	fun isEmpty(x: Int, y: Int, z: Int): Boolean {
		return getBlockID(x, y, z) != 0
	}

	internal fun getRaw(index: Int): Int {
		return data?.get(index) ?: filled
	}

	internal fun getRaw(x: Int, y: Int, z: Int): Int {
		return data?.get(indexOfBlock(x, y, z)) ?: filled
	}

	internal fun setRaw(index: Int, value: Int) {
		val data = ensureData()
		if(data[index] != value) {
			lastChangedMillis = System.currentTimeMillis()
			data[index] = value
		}
	}

	internal fun setRaw(x: Int, y: Int, z: Int, value: Int) {
		setRaw(indexOfBlock(x, y, z), value)
	}

	operator fun get(index: Int): BlockView {
		return get(xFromIndex(index), yFromIndex(index), zFromIndex(index))
	}

	operator fun get(x: Int, y: Int, z: Int): BlockView {
		return BlockView(this, x, y, z)
	}

	private fun ensureData(): IntArray {
		data?.let { return it }

		val array = IntArray(SIZE * SIZE * SIZE) { filled }
		data = array
		return array
	}

	internal fun debugBoundingBox() {
		val bounds = AABB(Vector3f(0f), Vector3f(SIZE.toFloat()))
		val pos = worldBlockPosition.toFloatVector()

		ClientPlayer.entity?.let { entity ->
			val entityBounds = entity.type.bounds

			val color = if(bounds.isIntersecting(pos, entityBounds, entity.position))
				Vector3f(1f, 0f, 0f)
			else Vector3f(1f, 1f, 0f)


			bounds.debugRender(pos, color)
		}

	}

	abstract class ChunkProxy(val chunk: Chunk): Proxy

	companion object {
		const val BITS = 5
		const val SIZE = 1 shl BITS
		const val MASK = SIZE - 1
		const val VOLUME = SIZE * SIZE * SIZE

		fun indexOfBlock(x: Int, y: Int, z: Int): Int {
			if (x < 0 || x >= SIZE || y < 0 || y >= SIZE || z < 0 || z >= SIZE) return -1
			return x + (y * SIZE) + (z * SIZE * SIZE)
		}

		fun xFromIndex(index: Int) = index % SIZE
		fun yFromIndex(index: Int) = (index / SIZE) % SIZE
		fun zFromIndex(index: Int) = ((index / SIZE) / SIZE) % SIZE
	}
}