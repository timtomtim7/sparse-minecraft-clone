package blue.sparse.minecraft.common.events.entity

import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.event.Event

interface EntityEvent: Event {
	val entity: Entity<*>
}