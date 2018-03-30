package blue.sparse.minecraft.common.world.generator

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.block.Block
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.world.Chunk
import org.lwjgl.stb.STBPerlin

object TestChunkGenerator: ChunkGenerator {
	override fun generate(chunkPosition: Vector3i, blocks: Array<Block?>) {
		if(chunkPosition.y <= 0)
			blocks.fill(Block(BlockType.stone))
		if(chunkPosition.y != 1)
			return

		val cx = chunkPosition.x shl Chunk.BITS
//		val cy = chunkPosition.y shl Chunk.BITS
		val cz = chunkPosition.z shl Chunk.BITS

		for(x in 0 until Chunk.SIZE) {
			for(z in 0 until Chunk.SIZE) {
//				for(y in 0 until Chunk.SIZE) {
//					val noise = STBPerlin.stb_perlin_fbm_noise3(
//							(cx + x) * 0.04f,
//							(cy + y) * 0.04f,
//							(cz + z) * 0.04f,
//							1024, 1024, 1024
//					)

//					val noise = Perlin.noise(
//							(cx + x).toFloat(),
//							(cy + y).toFloat(),
//							(cz + z).toFloat(),
//							4, 0.5f, 0.007f
//					)
//
//					if(noise < 0) {
//						blocks[Chunk.indexOfBlock(x, y, z)] = Block(BlockType.clay)
//					}
//				}
				val maxY = (STBPerlin.stb_perlin_noise3((cx + x) * 0.05f, (cz + z) * 0.05f, 0f, 1024, 1024, 1024) * 8 + 8).toInt()
				for(y in 0 until maxY) {
					blocks[Chunk.indexOfBlock(x, y, z)] = Block(BlockType.concretePowderGray)
				}
			}
		}
	}
}