package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.util.Proxy
import blue.sparse.minecraft.common.util.ProxyProvider
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

    private val entities = HashSet<Entity<*>>()

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
        return entities.add(entity)
    }

    fun despawnEntity(entity: Entity<*>): Boolean {
        return entities.remove(entity)
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

    operator fun get(x: Int, y: Int, z: Int): BlockView? {
        return getBlock(x, y, z)
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