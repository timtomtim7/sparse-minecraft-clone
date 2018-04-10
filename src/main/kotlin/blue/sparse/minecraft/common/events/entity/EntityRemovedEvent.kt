package blue.sparse.minecraft.common.events.entity

import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.event.Cancelable
import blue.sparse.minecraft.common.events.world.WorldEvent
import blue.sparse.minecraft.common.world.World

class EntityRemovedEvent(
		override val world: World,
		override val entity: Entity<*>
) : WorldEvent, EntityEvent, Cancelable {

	override var canceled = false

}