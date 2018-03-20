package blue.sparse.minecraft.common.world


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