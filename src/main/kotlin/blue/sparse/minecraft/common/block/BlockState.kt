package blue.sparse.minecraft.common.block

interface BlockState {
	val stateID: Int

	object Default: BlockState {
		override val stateID = 0
	}
}