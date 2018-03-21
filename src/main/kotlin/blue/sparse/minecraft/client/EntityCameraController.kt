package blue.sparse.minecraft.client

import blue.sparse.engine.render.camera.Camera
import blue.sparse.engine.render.camera.CameraController
import blue.sparse.minecraft.common.entity.Entity

class EntityCameraController(camera: Camera, val viewEntity: Entity<*>) : CameraController(camera) {

    override fun update(delta: Float) {
        camera.transform.setTranslation(viewEntity.position)
        camera.transform.setRotation(viewEntity.rotation)
    }

}