package blue.sparse.minecraft.common.block

//TODO: Should these be mutable?
data class Block(val type: BlockType, val state: BlockState = BlockState.None) {

	internal val rawID: Int = type.id or (state.stateID shl 12)

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
}