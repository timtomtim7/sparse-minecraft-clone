package blue.sparse.minecraft.common.block

//TODO: Should these be mutable?
data class Block(
		val type: BlockType? = null,
		val state: BlockState = BlockState.Default
) {

	val isOccluding = type != null && !type.transparent

	companion object {
		val empty = Block()
	}
}