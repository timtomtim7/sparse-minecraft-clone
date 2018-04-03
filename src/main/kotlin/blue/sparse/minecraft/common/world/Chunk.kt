package blue.sparse.minecraft.common.world

import blue.sparse.engine.math.int
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.biome.BiomeType
import blue.sparse.minecraft.common.block.Block
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.util.math.AABB
import blue.sparse.minecraft.common.util.proxy.Proxy
import blue.sparse.minecraft.common.util.proxy.ProxyProvider

class Chunk internal constructor(val region: Region, position: Vector3i, private var data: IntArray?) {

	private var filled: Int = 0

//	var lastChangedMillis: Long = System.currentTimeMillis()
//		private set

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

	constructor(region: Region, position: Vector3i): this(region, position, null)

	fun fill(type: Block) {
		data = null
		filled = type.rawID
	}

	/*
		Data getters by index
	 */

	internal fun getTypeID(index: Int): Int {
		return typeID(getRaw(index))
	}

	fun getType(index: Int): BlockType? {
		return type(getRaw(index))
	}

	internal fun getStateID(index: Int): Int {
		return stateID(getRaw(index))
	}

	//TODO: getState(index: Int)

	fun getBlockLight(index: Int): Int {
		return blockLight(getRaw(index))
	}

	fun getSkyLight(index: Int): Int {
		return skyLight(getRaw(index))
	}

	internal fun getBiomeID(index: Int): Int {
		return biomeID(getRaw(index))
	}

	fun getBiome(index: Int): BiomeType {
		return biome(getRaw(index))
	}

	/*
		Data getters by coordinate
	 */

	internal fun getTypeID(x: Int, y: Int, z: Int): Int {
		return getTypeID(indexOfBlock(x, y, z))
	}

	fun getType(x: Int, y: Int, z: Int): BlockType? {
		return getType(indexOfBlock(x, y, z))
	}

	internal fun getStateID(x: Int, y: Int, z: Int): Int {
		return getStateID(indexOfBlock(x, y, z))
	}

	//TODO: getState(x: Int, y: Int, z: Int)

	fun getBlockLight(x: Int, y: Int, z: int): Int {
		return getBlockLight(indexOfBlock(x, y, z))
	}

	fun getSkyLight(x: Int, y: Int, z: int): Int {
		return getSkyLight(indexOfBlock(x, y, z))
	}

	internal fun getBiomeID(x: Int, y: Int, z: int): Int {
		return getBiomeID(indexOfBlock(x, y, z))
	}

	fun getBiome(x: Int, y: Int, z: int): BiomeType {
		return getBiome(indexOfBlock(x, y, z))
	}

	/*
		Data setters by index
	 */

	internal fun setTypeID(index: Int, value: Int) {
		setRaw(index, typeID(getRaw(index), value))
	}

	fun setType(index: Int, value: BlockType?) {
		setRaw(index, type(getRaw(index), value))
	}

	internal fun setStateID(index: Int, value: Int) {
		setRaw(index, stateID(getRaw(index), value))
	}

	//TODO: getState(index: Int)

	fun setBlockLight(index: Int, value: Int) {
		setRaw(index, blockLight(getRaw(index), value))
	}

	fun setSkyLight(index: Int, value: Int) {
		setRaw(index, skyLight(getRaw(index), value))
	}

	internal fun setBiomeID(index: Int, value: Int) {
		setRaw(index, biomeID(getRaw(index), value))
	}

	fun setBiome(index: Int, value: BiomeType) {
		setRaw(index, biome(getRaw(index), value))
	}

	/*
		Data setters by coordinate
	 */
	internal fun setTypeID(x: Int, y: Int, z: Int, value: Int) {
		setTypeID(indexOfBlock(x, y, z), value)
	}

	fun setType(x: Int, y: Int, z: Int, value: BlockType?) {
		setType(indexOfBlock(x, y, z), value)
	}

	internal fun setStateID(x: Int, y: Int, z: Int, value: Int) {
		setStateID(indexOfBlock(x, y, z), value)
	}

	//TODO: getState(index: Int)

	fun setBlockLight(x: Int, y: Int, z: Int, value: Int) {
		setBlockLight(indexOfBlock(x, y, z), value)
	}

	fun setSkyLight(x: Int, y: Int, z: Int, value: Int) {
		setSkyLight(indexOfBlock(x, y, z), value)
	}

	internal fun setBiomeID(x: Int, y: Int, z: Int, value: Int) {
		setBiomeID(indexOfBlock(x, y, z), value)
	}

	fun setBiome(x: Int, y: Int, z: Int, value: BiomeType) {
		setBiome(indexOfBlock(x, y, z), value)
	}

	/*
		Raw getters and setters
	 */

	internal fun getRaw(index: Int): Int {
		return data?.get(index) ?: filled
	}

	internal fun getRaw(x: Int, y: Int, z: Int): Int {
		return data?.get(indexOfBlock(x, y, z)) ?: filled
	}

	internal fun setRaw(index: Int, value: Int) {
		val data = ensureData()
		val before = data[index]
		if(before != value) {
			data[index] = value
			proxy.changed(xFromIndex(index), yFromIndex(index), zFromIndex(index), before, value)
			region.accessed()
		}
	}

	internal fun setRaw(x: Int, y: Int, z: Int, value: Int) {
		setRaw(indexOfBlock(x, y, z), value)
	}

	/*
		Other
	 */

	fun isEmpty(x: Int, y: Int, z: Int): Boolean {
		return getTypeID(x, y, z) != 0
	}

	operator fun get(index: Int): BlockView {
		return get(xFromIndex(index), yFromIndex(index), zFromIndex(index))
	}

	operator fun get(chunkBlock: Vector3i): BlockView {
		return get(chunkBlock.x, chunkBlock.y, chunkBlock.z)
	}

	operator fun get(x: Int, y: Int, z: Int): BlockView {
		return BlockView(this, x, y, z)
	}

//	fun setAll(x: Int, y: Int, z: Int, type: BlockType? = getType(x, y, z), state: BlockState = BlockState.Default) {
//
//	}

	private fun ensureData(): IntArray {
		data?.let { return it }

		val array = IntArray(SIZE * SIZE * SIZE) { filled }
		data = array
		return array
	}

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

	abstract class ChunkProxy(val chunk: Chunk): Proxy {
		abstract fun changed(x: Int, y: Int, z: Int, before: Int, new: Int)

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

		internal fun typeID(raw: Int): Int {
			return (raw shr 0) and 0xFFF
		}

		internal fun typeID(raw: Int, value: Int): Int {
			return (raw.inv() or 0xFFF).inv() or value
		}

		internal fun type(raw: Int): BlockType? {
//			return typeID(raw).takeIf { it > 0 }?.let { BlockType[it] }
			return BlockType[typeID(raw)]
		}

		internal fun type(raw: Int, value: BlockType?): Int {
			return typeID(raw, value?.id ?: 0)
		}

		internal fun stateID(raw: Int): Int {
			return (raw shr 12) and 0xF
		}

		internal fun stateID(raw: Int, value: Int): Int {
			return (raw.inv() or (0xF shl 12)).inv() or (value shl 12)
		}

		internal fun blockLight(raw: Int): Int {
			return (raw shr 16) and 0xF
		}

		internal fun blockLight(raw: Int, value: Int): Int {
			return (raw.inv() or (0xF shl 16)).inv() or (value shl 16)
		}

		internal fun skyLight(raw: Int): Int {
			return (raw shr 20) and 0xF
		}

		internal fun skyLight(raw: Int, value: Int): Int {
			return (raw.inv() or (0xF shl 20)).inv() or (value shl 20)
		}

		internal fun biomeID(raw: Int): Int {
			return (raw shr 24) and 0xFF
		}

		internal fun biomeID(raw: Int, value: Int): Int {
			return (raw.inv() or (0xFF shl 24)).inv() or (value shl 24)
		}

		internal fun biome(raw: Int): BiomeType {
			return BiomeType[biomeID(raw)] ?: BiomeType.void
		}

		internal fun biome(raw: Int, value: BiomeType?): Int {
			return biomeID(raw, value?.id ?: 0)
		}
	}
}