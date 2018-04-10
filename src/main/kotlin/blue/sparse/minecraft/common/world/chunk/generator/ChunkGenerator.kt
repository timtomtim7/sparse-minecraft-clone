package blue.sparse.minecraft.common.world.chunk.generator

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.biome.BiomeType
import blue.sparse.minecraft.common.block.Block
import blue.sparse.minecraft.common.world.chunk.ChunkElementStorage

interface ChunkGenerator {
	fun generate(chunkPosition: Vector3i, blocks: ChunkElementStorage<Block>, biomes: ChunkElementStorage<BiomeType>)

//	companion object {
//		internal val blocks = ThreadLocal.withInitial { Array(Chunk.VOLUME) { Block.empty } }
//	}
}