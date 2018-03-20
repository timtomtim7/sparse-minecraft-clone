package blue.sparse.minecraft.common.entity.impl.types

import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.entity.EntityType
import blue.sparse.minecraft.common.entity.data.EntityData
import blue.sparse.minecraft.common.item.ItemStack
import blue.sparse.minecraft.common.util.ProxyProvider

object EntityTypeItem : EntityType("item") {

	override val proxy: EntityTypeProxy by ProxyProvider<EntityTypeProxy>(
			"blue.sparse.minecraft.client.entity.proxy.ClientEntityTypeItemProxy",
			"blue.sparse.minecraft.server.entity.proxy.Default",
			this
	)

	override fun createData(): EntityData {
		return Data(ItemStack(BlockType.stone.item!!))
	}

	class Data(var stack: ItemStack<*>): EntityData

}