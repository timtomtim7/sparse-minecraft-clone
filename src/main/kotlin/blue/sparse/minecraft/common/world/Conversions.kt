package blue.sparse.minecraft.common.world

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.util.shl


/*
	chunk -> region
		block
	region -> chunk
		block
	region -> world
		block
		chunk
	world -> region
		block
		chunk

	---------------

	chunk -> world
		block
	world -> chunk
		block

 */

internal fun chunkBlockToRegionBlock(rc: Int, cb: Int): Int {
	return (rc shl Chunk.BITS) or cb
}

internal fun regionBlockToChunkBlock(rb: Int): Int {
	return rb and Chunk.MASK
}

internal fun regionBlockToWorldBlock(wr: Int, rb: Int): Int {
	return (wr shl (Region.BITS + Chunk.BITS)) or rb
}

internal fun chunkBlockToWorldBlock(wr: Int, rc: Int, cb: Int): Int {
	return regionBlockToWorldBlock(wr, chunkBlockToRegionBlock(rc, cb))
}

internal fun worldChunkToRegionChunk(wc: Int): Int {
	return wc and Region.MASK
}

internal fun regionChunkToWorldChunk(wr: Int, rc: Int): Int {
	return (wr shl Region.BITS) or rc
}

// --------------------------------------------- \\



internal fun worldChunkToWorldRegion(wc: Int): Int {
	return wc shr Region.BITS
}

internal fun worldBlockToChunkBlock(wb: Int): Int {
	return wb and Chunk.MASK
}

internal fun worldBlockToWorldChunk(wb: Int): Int {
	return wb shr Chunk.BITS
}

internal fun worldChunkToWorldRegion(wc: Vector3i): Vector3i {
	return Vector3i(
			worldChunkToWorldRegion(wc.x),
			worldChunkToWorldRegion(wc.y),
			worldChunkToWorldRegion(wc.z)
	)
}

internal fun worldChunkToRegionChunk(wc: Vector3i): Vector3i {
	return Vector3i(
			worldChunkToRegionChunk(wc.x),
			worldChunkToRegionChunk(wc.y),
			worldChunkToRegionChunk(wc.z)
	)
}

internal fun worldChunkToWorldBlock(wc: Vector3i): Vector3i {
	return wc shl Chunk.BITS
}

//val worldChunkPosition = run {
//	val wr = region.worldRegionPosition
//	val rc = regionChunkPosition
//	Vector3i(regionChunkToWorldChunk(wr.x, rc.x), regionChunkToWorldChunk(wr.y, rc.y), regionChunkToWorldChunk(wr.z, rc.z))
//}
//	get() = field.clone()
//
//val worldBlockPosition = run {
//	val wr = region.worldRegionPosition
//	val rc = regionChunkPosition
//	Vector3i(chunkBlockToWorldBlock(wr.x, rc.x, 0), chunkBlockToWorldBlock(wr.y, rc.y, 0),chunkBlockToWorldBlock(wr.z, rc.z, 0))
//}
//	get() = field.clone()