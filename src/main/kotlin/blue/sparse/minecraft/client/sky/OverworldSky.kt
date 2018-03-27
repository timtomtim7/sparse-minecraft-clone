package blue.sparse.minecraft.client.sky

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.render.camera.Camera
import blue.sparse.engine.render.resource.Texture
import blue.sparse.engine.render.scene.component.ShaderSkybox
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.client.CelestialBodyComponent
import blue.sparse.minecraft.client.util.BlankShader

class OverworldSky : Sky {
	private val sunTexture = Texture(Asset["minecraft/textures/environment/sun.png"]).apply {
		nearestFiltering()
		clampToEdge()
	}
	private val moonTexture = Texture(Asset["minecraft/textures/environment/moon_full.png"]).apply {
		nearestFiltering()
		clampToEdge()
	}

	val sun = CelestialBodyComponent(sunTexture, scale = 0.8f, initialRotation = -45f)
	val moon = CelestialBodyComponent(moonTexture, scale = 0.65f, initialRotation = -180f + -45f)

	val skybox = ShaderSkybox(Asset["minecraft/shaders/world/sky/sky.fs"]) {
		uniforms["uSunDirection"] = sun.transform.rotation.forward
		uniforms["uGravity"] = Vector3f(0f, -1f, 0f)
	}

	override fun render(camera: Camera, delta: Float) {
		skybox.render(delta, camera, BlankShader.shader)
		sun.render(delta, camera, BlankShader.shader)
		moon.render(delta, camera, BlankShader.shader)
	}
}