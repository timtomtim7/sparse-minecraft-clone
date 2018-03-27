package blue.sparse.minecraft.client

import blue.sparse.engine.render.camera.Camera
import blue.sparse.engine.render.camera.CameraController
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.impl.types.living.EntityTypeLiving

class EntityCameraController(camera: Camera, val viewEntity: Entity<*>) : CameraController(camera) {

    override fun update(delta: Float) {
        val eyeHeight = (viewEntity.type as? EntityTypeLiving)?.eyeHeight ?: 0f

		camera.transform.setTranslation(viewEntity.interpolatedPosition + Vector3f(0f, eyeHeight, 0f))
        camera.transform.setRotation(viewEntity.rotation)
    }

}