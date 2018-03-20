package blue.sparse.minecraft.common.world

import blue.sparse.engine.SparseEngine
import blue.sparse.math.vectors.floats.*
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.util.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class World(val name: String, val id: UUID = UUID.randomUUID()) {

	private val regions = ConcurrentHashMap<Vector3i, Region>()
	private val key = ThreadLocal.withInitial { Vector3i(0) }

	val proxy by ProxyProvider.invoke<WorldProxy>(
			"blue.sparse.minecraft.client.world.proxy.ClientWorldProxy",
			"blue.sparse.minecraft.server.world.proxy.ServerWorldProxy",
			this
	)

	private val _entities = HashSet<Entity<*>>()

	val entities: Set<Entity<*>>
		get() = _entities

	val loadedChunks: Collection<Chunk>
		get() = regions.values.flatMap(Region::loadedChunks)

	fun getRegion(x: Int, y: Int, z: Int): Region {
		val key = this.key.get()
		key.assign(x, y, z)

		return regions.getOrPut(key) { Region(this, key.clone()) }
	}

    fun <T : EntityType> spawnEntity(entityType: T, position: Vector3f): Entity<T> {
        val entity = Entity(entityType, position, this)
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

    //TODO: Look at all this mostly repeated code! Terrible.

	fun testBlockIntersections(bounds: AABB, position: Vector3f, movement: Vector3f): Vector3f {
		val min = floor(bounds.min + position - 1f).toIntVector()
		val max = ceil(bounds.max + position + 1f).toIntVector()

//		val result = Vector3f(1f)
		val result = movement

		for(x in min.x..max.x) {
			for(y in min.y..max.y) {
				for(z in min.z..max.z) {
					val type = getBlock(x, y, z)?.type ?: continue
					val blockBounds = type.boundingBox
					val blockPosition = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

					blockBounds.testIntersection(blockPosition, movement, bounds, position)
					if(result.all { it == 0f })
						return result
				}
			}
		}

		return result
	}

	internal fun debugRenderInteresections(bounds: AABB, position: Vector3f, color: Vector3f) {
		val min = floor(bounds.min + position - 1f).toIntVector()
		val max = ceil(bounds.max + position + 1f).toIntVector()

//		val result = Vector3f(1f)
		for(x in min.x..max.x) {
			for(y in min.y..max.y) {
				for(z in min.z..max.z) {
					val type = getBlock(x, y, z)?.type ?: continue
					val blockBounds = type.boundingBox
					val blockPosition = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

					val intersection = blockBounds.testIntersection(blockPosition, SparseEngine.game.camera.transform.rotation.forward, bounds, position)
					blockBounds.debugRender(blockPosition, intersection)

				}
			}
		}
	}

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

	operator fun get(x: Int, y: Int, z: Int): BlockView? {
		return getBlock(x, y, z)
	}

	abstract class WorldProxy(val world: World): Proxy

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