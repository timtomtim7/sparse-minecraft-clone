package blue.sparse.minecraft.client.util

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.asset.Asset
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.math.vectors.floats.Vector3f
import org.lwjgl.opengl.GL11.glLineWidth

object Debug {

	private val shader = ShaderProgram(
			Asset["minecraft/shaders/debug/line.fs"],
			Asset["minecraft/shaders/nothing.vs"],
			Asset["minecraft/shaders/debug/line.gs"]
	)

	fun drawLine(start: Vector3f, end: Vector3f, color: Vector3f = Vector3f(1f, 0f, 0f)) {
//		glCall { glDisable(GL_DEPTH_TEST) }
		glCall { glLineWidth(4f) }
		shader.bind {
			//			uniforms["uModel"] = Matrix4f.identity()
			uniforms["uViewProj"] = SparseEngine.game.camera.viewProjectionMatrix
			uniforms["uStart"] = start
			uniforms["uEnd"] = end
			uniforms["uColor"] = color
			BlankModel.render()
		}
//		glCall { glEnable(GL_DEPTH_TEST) }
	}
}