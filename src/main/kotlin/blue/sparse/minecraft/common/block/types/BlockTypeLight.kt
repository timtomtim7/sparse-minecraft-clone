package blue.sparse.minecraft.common.block.types

import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.util.Identifier

abstract class BlockTypeLight(identifier: Identifier) : BlockType(identifier) {
	//	override val lightEmission get() = Vector3i(random.nextInt(16), random.nextInt(16), random.nextInt(16))
	override val lightEmission get() = Vector3i(15)

	constructor(identifier: String) : this(Identifier(identifier))
}