package blue.sparse.minecraft.common.entity.impl.types

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.entity.data.EntityData
import blue.sparse.minecraft.common.item.ItemStack
import blue.sparse.minecraft.common.util.math.AABB
import blue.sparse.minecraft.common.util.proxy.ProxyProvider

object EntityTypeItem : EntityType("item") {

	override val proxy: EntityTypeProxy by ProxyProvider<EntityTypeProxy>(
			"blue.sparse.minecraft.client.entity.proxy.ClientEntityTypeItemProxy",
			"blue.sparse.minecraft.server.entity.proxy.Default",
			this
	)

	override val bounds = AABB(Vector3f(-0.25f / 2f), Vector3f(0.25f) / 2f)

	override fun createData(): EntityData {
		return Data(ItemStack(BlockType.stone.item!!))
	}

	class Data(var stack: ItemStack<*>) : EntityData

}