package blue.sparse.minecraft.client.entity.proxy

import blue.sparse.engine.render.camera.Camera
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.EntityType

abstract class ClientEntityTypeProxy(entityType: EntityType) : EntityType.EntityTypeProxy(entityType) {
	abstract fun render(entity: Entity<*>, camera: Camera, delta: Float)
}