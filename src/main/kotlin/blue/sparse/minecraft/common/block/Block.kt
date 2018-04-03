package blue.sparse.minecraft.common.block

import blue.sparse.minecraft.common.biome.BiomeType
import blue.sparse.minecraft.common.world.Chunk

//TODO: Should these be mutable?
data class Block(
		val type: BlockType? = null,
		val state: BlockState = BlockState.Default,
		val biome: BiomeType = BiomeType.void
) {

	internal val rawID: Int = Chunk.biome(Chunk.stateID(Chunk.type(0, type), state.stateID), biome)

//	internal val rawID: Int = type.id or (state.stateID shl 12)

//	internal var typeID: Int
//		get() = (raw shr 0) and 0xFFF
//		set(value) {
//			raw = (raw.inv() or 0xFFF).inv() or value
//		}
//
//	internal var stateID: Int
//		get() = (raw shr 12) and 0xF
//		set(value) {
//			raw = (raw.inv() or (0xF shl 12)).inv() or (value shl 12)
//		}

	companion object {
		val empty = Block()
	}
}