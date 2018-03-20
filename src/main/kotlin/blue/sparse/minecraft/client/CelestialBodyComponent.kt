package blue.sparse.minecraft.client

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.render.camera.Camera
import blue.sparse.engine.render.resource.Texture
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.model.*
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.engine.render.scene.component.Transformed
import blue.sparse.math.vectors.floats.Vector2f
import blue.sparse.math.vectors.floats.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_ONE
import org.lwjgl.opengl.GL11.GL_SRC_ALPHA

class CelestialBodyComponent(
		val texture: Texture,
		var rotationAxis: Vector3f = Vector3f(1f, 0f, 0f),
		var rotationSpeed: Float = -0.5f,
		var scale: Float = 1f,
		initialRotation: Float = -30f
) : Transformed() {
	override val overridesShader = true

	val direction: Vector3f
		get() = transform.rotation.forward

	init {
//		transform.setScale(Vector3f(scale))
//		println(scale)
//		transform.rotateDeg(cross(rotationAxis, Vector3f(0f, 1f, 0f)), 45f)
		transform.rotateDeg(Vector3f(0f, 1f, 0f), 30f)
		transform.rotateDeg(rotationAxis, initialRotation)
	}

	override fun render(delta: Float, camera: Camera, shader: ShaderProgram) {
//		shader.uniforms["uLightDirection"] = direction
		glCall { GL11.glEnable(GL11.GL_BLEND) }
		glCall { GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE) }
		glCall { GL11.glDisable(GL11.GL_DEPTH_TEST) }
		glCall { GL11.glDepthMask(false) }

		transform.rotateDeg(rotationAxis, delta * rotationSpeed)
//		transform.setRotation(Quaternion4f.lookAt(direction, Vector3f(0f, 1f, 0f)))

		Companion.shader.bind {
			texture.bind(0)
			uniforms["uScale"] = scale
			uniforms["uTexture"] = 0
			uniforms["uModel"] = modelMatrix
			uniforms["uProjection"] = camera.projection
			uniforms["uRotation"] = camera.transform.rotation.conjugate.toMatrix()
//			uniforms["uScale"] = scale
//			uniformSetter()
			model.render()
		}

		glCall { GL11.glDepthMask(true) }
		glCall { GL11.glEnable(GL11.GL_DEPTH_TEST) }
		glCall { GL11.glDisable(GL11.GL_BLEND) }
	}

	companion object {
		private val model: IndexedModel
		private val shader = ShaderProgram(Asset["minecraft/shaders/celestial.fs"], Asset["minecraft/shaders/celestial.vs"])

		init {
			val array = VertexArray()
			val buffer = VertexBuffer()
			val layout = VertexLayout()

			layout.add<Vector3f>()
			layout.add<Vector2f>()

			buffer.add(Vector3f(-1f, -1f, 2.5f), Vector2f(0f, 1f))
			buffer.add(Vector3f(-1f, +1f, 2.5f), Vector2f(0f, 0f))
			buffer.add(Vector3f(+1f, +1f, 2.5f), Vector2f(1f, 0f))
			buffer.add(Vector3f(+1f, -1f, 2.5f), Vector2f(1f, 1f))

			array.add(buffer, layout)

			model = IndexedModel(array, intArrayOf(0, 1, 2, 0, 2, 3))
		}
	}
}