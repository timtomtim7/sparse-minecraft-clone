package blue.sparse.minecraft.client.sky

import blue.sparse.engine.render.camera.Camera

interface Sky {
	fun render(camera: Camera, delta: Float)
}