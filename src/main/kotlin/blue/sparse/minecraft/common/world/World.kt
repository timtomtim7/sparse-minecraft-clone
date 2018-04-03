package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.floats.*
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.event.post
import blue.sparse.minecraft.common.events.world.*
import blue.sparse.minecraft.common.player.Player
import blue.sparse.minecraft.common.util.*
import blue.sparse.minecraft.common.util.math.*
import blue.sparse.minecraft.common.util.proxy.Proxy
import blue.sparse.minecraft.common.util.proxy.ProxyProvider
import blue.sparse.minecraft.common.world.generator.ChunkGenerator
import blue.sparse.minecraft.common.world.generator.thread.ChunkGenerationThread
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.experimental.buildSequence

class World(val name: String, val id: UUID = UUID.randomUUID(), val generator: ChunkGenerator) {

	val proxy by ProxyProvider.invoke<WorldProxy>(
			"blue.sparse.minecraft.client.world.proxy.ClientWorldProxy",
			"blue.sparse.minecraft.server.world.proxy.ServerWorldProxy",
			this
	)

	private val regions = ConcurrentHashMap<Vector3i, Region>()

	private val key by threadLocal { Vector3i(0) }

	private val chunkGenerationThread: ChunkGenerationThread

	private var lastUnloadCheck = System.currentTimeMillis()

	private val _entities = ConcurrentHashMap.newKeySet<Entity<*>>()

	val entities: Set<Entity<*>>
		get() = _entities

	val loadedChunks: Collection<Chunk>
		get() = regions.values.flatMap(Region::loadedChunks)

	val players: Set<Player>
		get() = Minecraft.players.filterTo(HashSet()) { it.entity?.world == this }

	init {
		WorldInitializationEvent(this).post()
		chunkGenerationThread = ChunkGenerationThread(this, buildSequence {
			while(true) {
				for (player in players) {
					val renderDistance = player.renderDistance
					yield(renderDistance.firstOrNull { getChunk(it) == null } ?: continue)
				}
			}
		})
		chunkGenerationThread.start()
	}

	fun <T : EntityType> addEntity(entityType: T, position: Vector3f): Entity<T> {
		val entity = Entity(entityType, this, position)
		addEntity(entity)
		return entity
	}

	fun addEntity(entity: Entity<*>): Boolean {
		if(entity in _entities)
			return false

		val event = WorldEntityAddedEvent(this, entity)
		Minecraft.events.post(event)
		if(event.canceled)
			return false

		return _entities.add(entity)
	}

	fun removeEntity(entity: Entity<*>): Boolean {
		if(entity !in _entities)
			return false

		val event = WorldEntityRemovedEvent(this, entity)
		Minecraft.events.post(event)
		if(event.canceled)
			return false

		return _entities.remove(entity)
	}

	fun update(delta: Float) {
//		var generate = 1
//		for (player in Minecraft.players) {
//			val renderDistance = player.renderDistance
//			for(chunkPos in renderDistance) {
//				if(getChunk(chunkPos.x, chunkPos.y, chunkPos.z) != null || generate <= 0)
//					continue
//
//				getOrGenerateChunk(chunkPos.x, chunkPos.y, chunkPos.z)
//				generate--
//			}
//		}

		val now = System.currentTimeMillis()
		if(now - lastUnloadCheck > 2500L) {
			lastUnloadCheck = now
			val toUnload = regions.values.filter { now - it.lastAccessTime > 5000L }
			toUnload.forEach(Region::unloaded)
			regions.values.removeAll(toUnload)
		}

		entities.forEach { it.update(delta) }
	}

	fun testBlockIntersections(bounds: AABB, position: Vector3f, movement: Vector3f): Vector3f {
		val min = floor(bounds.min + position - 1f).toIntVector()
		val max = ceil(bounds.max + position + 1f).toIntVector()

		val volume = (max - min).run { x * y * z }

		val result = Vector3f(1f)

		for(i in 0 until volume) {
			val pos = SphericalBlockOrder[i, (max - min) / 2 + min]
			val type = getBlock(pos.x, pos.y, pos.z)?.type ?: continue
			val blockBounds = type.bounds
			val blockPosition = pos.toFloatVector()

			result *= blockBounds.testIntersection(blockPosition, movement, bounds, position)

//			if (movement.all { it == 0f })
//				return movement
		}

		return result
	}

	fun getRegion(x: Int, y: Int, z: Int) = getRegion(key(x, y, z))

	fun getRegion(key: Vector3i): Region {
		var region = regions[key]
		if(region == null) {
			val copiedKey = key.clone()
			region = Region(this, copiedKey)
			regions[copiedKey] = region
		}

		return region
	}

	fun getChunk(worldChunk: Vector3i): Chunk? {
		return getRegion(worldChunkToWorldRegion(worldChunk)).getChunk(worldChunkToRegionChunk(worldChunk))
	}

	fun getChunk(x: Int, y: Int, z: Int): Chunk? {
		return getChunk(key(x, y, z))
	}

	fun getOrGenerateChunk(worldChunk: Vector3i): Chunk {
		return getRegion(worldChunkToWorldRegion(worldChunk)).getOrGenerateChunk(worldChunkToRegionChunk(worldChunk))
	}

	fun getOrGenerateChunk(x: Int, y: Int, z: Int): Chunk {
		return getOrGenerateChunk(key(x, y, z))
	}

	fun getBlock(worldBlock: Vector3i): BlockView? {
		return getChunk(worldBlockToWorldChunk(worldBlock))?.get(worldBlockToChunkBlock(worldBlock))
	}

	fun getBlock(x: Int, y: Int, z: Int): BlockView? {
		return getBlock(key(x, y, z))
	}

	fun getOrGenerateBlock(worldBlock: Vector3i): BlockView {
		return getOrGenerateChunk(worldBlockToWorldChunk(worldBlock))[worldBlockToChunkBlock(worldBlock)]
	}

	fun getOrGenerateBlock(x: Int, y: Int, z: Int): BlockView {
		return getOrGenerateBlock(key(x, y, z))
	}

	//TODO: Use a system which doesn't risk skipping blocks
	fun getTargetBlock(origin: Vector3f, direction: Vector3f, maxDistance: Float): TargetBlock? {
		val step = 0.1f
		val steps = (maxDistance / step).toInt()

		val position = origin.clone()

		var last = floor(position).toIntVector()

		for(i in 1..steps) {
			val vec = floor(position).toIntVector()
			val block = getBlock(vec.x, vec.y, vec.z)

			if(block?.type != null) {
				val diff = last - vec
				// A silly hack for avoiding diagonal faces
				if(diff.x != 0 && diff.y != 0) diff.x = 0
				if(diff.y != 0 && diff.z != 0) diff.z = 0

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

	private fun key(x: Int, y: Int, z: Int): Vector3i {
		return key.apply { assign(x, y, z) }
	}

	abstract class WorldProxy(val world: World) : Proxy
}