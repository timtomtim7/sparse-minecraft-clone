package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.floats.*
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.util.*
import blue.sparse.minecraft.common.world.generator.ChunkGenerator
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class World(val name: String, val id: UUID = UUID.randomUUID(), val generator: ChunkGenerator) {

	private val regions = ConcurrentHashMap<Vector3i, Region>()
	private val key = ThreadLocal.withInitial { Vector3i(0) }

	val proxy by ProxyProvider.invoke<WorldProxy>(
			"blue.sparse.minecraft.client.world.proxy.ClientWorldProxy",
			"blue.sparse.minecraft.server.world.proxy.ServerWorldProxy",
			this
	)

//	private val _entities = HashSet<Entity<*>>()
	private val _entities = ConcurrentHashMap.newKeySet<Entity<*>>()

	val entities: Set<Entity<*>>
		get() = _entities

	val loadedChunks: Collection<Chunk>
		get() = regions.values.flatMap(Region::loadedChunks)

	fun getRegion(x: Int, y: Int, z: Int): Region {
		val key = this.key.get()
		key.assign(x, y, z)

		var region = regions[key]
		if(region == null) {
			val copiedKey = key.clone()
			region = Region(this, copiedKey)
			regions[copiedKey] = region
		}

		return region
	}

	fun <T : EntityType> spawnEntity(entityType: T, position: Vector3f): Entity<T> {
		val entity = Entity(entityType, this, position)
		spawnEntity(entity)
		return entity
	}

	fun spawnEntity(entity: Entity<*>): Boolean {
		return _entities.add(entity)
	}

	fun despawnEntity(entity: Entity<*>): Boolean {
		return _entities.remove(entity)
	}

	fun update(delta: Float) {
		entities.forEach { it.update(delta) }
	}

	fun testBlockIntersections(bounds: AABB, position: Vector3f, movement: Vector3f): Vector3f {
		val min = floor(bounds.min + position - 1f).toIntVector()
		val max = ceil(bounds.max + position + 1f).toIntVector()

		val volume = (max - min).run { x * y * z }

		val result = Vector3f(1f)

		for(i in 0 until volume) {
			val pos = BlockLoop[i, (max - min) / 2 + min]
			val type = getBlock(pos.x, pos.y, pos.z)?.type ?: continue
			val blockBounds = type.boundingBox
			val blockPosition = pos.toFloatVector()

			val unaffected = blockBounds.testIntersection(blockPosition, movement, bounds, position)
			result *= unaffected
//			blockBounds.debugRender(blockPosition, unaffected)

			if (movement.all { it == 0f })
				return movement
		}

		return result
	}

	//TODO: Look at all this mostly repeated code! Terrible.
	fun getChunk(x: Int, y: Int, z: Int): Chunk? {
		val worldRegionX = worldChunkToWorldRegion(x)
		val worldRegionY = worldChunkToWorldRegion(y)
		val worldRegionZ = worldChunkToWorldRegion(z)

		val region = getRegion(worldRegionX, worldRegionY, worldRegionZ)
		val regionChunkX = worldChunkToRegionChunk(x)
		val regionChunkY = worldChunkToRegionChunk(y)
		val regionChunkZ = worldChunkToRegionChunk(z)

		return region.getChunk(regionChunkX, regionChunkY, regionChunkZ)
	}

	fun getOrGenerateChunk(x: Int, y: Int, z: Int): Chunk {
		val worldRegionX = worldChunkToWorldRegion(x)
		val worldRegionY = worldChunkToWorldRegion(y)
		val worldRegionZ = worldChunkToWorldRegion(z)

		val region = getRegion(worldRegionX, worldRegionY, worldRegionZ)
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

	//TODO: Use a system which doesn't risk skipping blocks
	fun getTargetBlock(origin: Vector3f, direction: Vector3f, maxDistance: Float): TargetBlock? {
		val step = 0.1f
		val steps = (maxDistance / step).toInt()

		val position = origin.clone()

//		Debug.addTempLine(position, position + direction)

		var last = floor(position).toIntVector()

		for(i in 1..steps) {
			val vec = floor(position).toIntVector()
			val block = getBlock(vec.x, vec.y, vec.z)
//			println(vec.joinToString())

			if(block?.type != null) {
				val diff = last - vec
				if(diff.x != 0 && diff.y != 0)
					diff.x = 0
				if(diff.y != 0 && diff.z != 0)
					diff.z = 0

//				println(diff)

				val face = BlockFace[diff] ?: continue
				return TargetBlock(block, face)
			}

			last = vec
			position += direction * step
		}

		return null
	}

	operator fun get(x: Int, y: Int, z: Int): BlockView? {
		return getBlock(x, y, z)
	}

	operator fun get(blockPosition: Vector3i): BlockView? {
		return getBlock(blockPosition.x, blockPosition.y, blockPosition.z)
	}

	abstract class WorldProxy(val world: World) : Proxy

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