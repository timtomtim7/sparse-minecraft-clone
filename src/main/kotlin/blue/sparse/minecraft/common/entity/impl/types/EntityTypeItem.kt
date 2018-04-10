package blue.sparse.minecraft.common.entity.impl.types

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.floats.lengthSquared
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.entity.Entity
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

	override fun update(entity: Entity<*>, delta: Float) {
		if(entity.timeSinceSpawned < 0.5f)
			return
		for (player in entity.world.players) {
			val playerEntity = player.entity ?: continue

//			if(playerEntity.type.bounds.isIntersecting(playerEntity.position, bounds, entity.position)) {
			if(lengthSquared(playerEntity.position - entity.position) < 2 * 2) {
				val item = (entity.data as Data).stack
				val remaining = player.inventory.addStack(item)
				item.amount = remaining
				if(item.amount <= 0)
					entity.remove()
			}
		}
	}

}

var Entity<EntityTypeItem>.stack
	get() = (data as EntityTypeItem.Data).stack
	set(value) {
		(data as EntityTypeItem.Data).stack = value
	}