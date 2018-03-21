package blue.sparse.minecraft.common.world.generator

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.block.Block
import blue.sparse.minecraft.common.world.Chunk

interface ChunkGenerator {
	fun generate(chunkPosition: Vector3i, blocks: Array<Block?>)

	companion object {
		internal val blocks = ThreadLocal.withInitial { Array<Block?>(Chunk.VOLUME) { null } }
	}
}