package blue.sparse.minecraft.common.block

interface BlockState {
	val stateID: Int

	object None: BlockState {
		override val stateID = 0
	}
}