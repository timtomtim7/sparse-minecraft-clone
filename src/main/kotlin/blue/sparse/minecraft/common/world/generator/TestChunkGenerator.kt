package blue.sparse.minecraft.common.world.generator

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.block.Block
import blue.sparse.minecraft.common.block.BlockType

object TestChunkGenerator: ChunkGenerator {
	override fun generate(chunkPosition: Vector3i, blocks: Array<Block?>) {
		if(chunkPosition.y <= 0)
			blocks.fill(Block(BlockType.lapisBlock))
	}
}