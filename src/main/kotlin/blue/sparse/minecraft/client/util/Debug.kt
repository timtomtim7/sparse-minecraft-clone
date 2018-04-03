package blue.sparse.minecraft.client.util

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.asset.Asset
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.math.vectors.floats.Vector3f
import org.lwjgl.opengl.GL11.*
import java.util.concurrent.ConcurrentLinkedQueue

object Debug {

	private val shader = ShaderProgram(
			Asset["minecraft/shaders/debug/line.fs"],
			Asset["minecraft/shaders/nothing.vs"],
			Asset["minecraft/shaders/debug/line.gs"]
	)

	private val temp = ConcurrentLinkedQueue<DebugElement>()

	interface DebugElement {
		fun render()
	}

	data class Line(val start: Vector3f, val end: Vector3f, val color: Vector3f, val lineWidth: Float) : DebugElement {
		override fun render() {
			drawLine(start, end, color, lineWidth)
		}
	}

	data class Cube(val start: Vector3f, val end: Vector3f, val color: Vector3f, val lineWidth: Float) : DebugElement {
		override fun render() {
			drawCube(start, end, color, lineWidth)
		}
	}

	fun addTempLine(start: Vector3f, end: Vector3f, color: Vector3f = Vector3f(1f, 0f, 0f), lineWidth: Float = 4f) {
		temp.add(Line(start, end, color, lineWidth))
	}

	fun addTempCube(start: Vector3f, end: Vector3f, color: Vector3f = Vector3f(0f, 0f, 1f), lineWidth: Float = 4f) {
		temp.add(Cube(start, end, color, lineWidth))
	}

	fun renderTemp() {
		glCall { glEnable(GL_BLEND) }
		glCall { glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) }
		temp.forEach(DebugElement::render)
		glCall { glDisable(GL_BLEND) }
		temp.clear()
	}

	fun drawLine(start: Vector3f, end: Vector3f, color: Vector3f = Vector3f(1f, 0f, 0f), lineWidth: Float = 4f) {
		glCall { glDisable(GL_DEPTH_TEST) }
		glCall { glEnable(GL_LINE_SMOOTH) }
		glCall { glLineWidth(lineWidth) }
		glCall { glHint(GL_LINE_SMOOTH_HINT, GL_NICEST) }
		shader.bind {
			uniforms["uViewProj"] = SparseEngine.game.camera.viewProjectionMatrix
			uniforms["uStart"] = start
			uniforms["uEnd"] = end
			uniforms["uColor"] = color
			BlankModel.render()
		}
		glCall { glEnable(GL_DEPTH_TEST) }
	}

	fun drawCube(start: Vector3f, end: Vector3f, color: Vector3f = Vector3f(0f, 0f, 1f), lineWidth: Float = 4f) {
		glCall { glDisable(GL_DEPTH_TEST) }
		glCall { glLineWidth(lineWidth) }
		shader.bind {
			uniforms["uViewProj"] = SparseEngine.game.camera.viewProjectionMatrix
			uniforms["uColor"] = color

			fun render(a: Vector3f, b: Vector3f) {
				uniforms["uStart"] = a
				uniforms["uEnd"] = b
				BlankModel.render()
			}

			render(Vector3f(start.x, start.y, start.z), Vector3f(start.x, end.y, start.z))
			render(Vector3f(end.x, start.y, start.z), Vector3f(end.x, end.y, start.z))
			render(Vector3f(end.x, start.y, end.z), Vector3f(end.x, end.y, end.z))
			render(Vector3f(start.x, start.y, end.z), Vector3f(start.x, end.y, end.z))

			render(Vector3f(start.x, start.y, start.z), Vector3f(end.x, start.y, start.z))
			render(Vector3f(start.x, end.y, start.z), Vector3f(end.x, end.y, start.z))
			render(Vector3f(start.x, end.y, end.z), Vector3f(end.x, end.y, end.z))
			render(Vector3f(start.x, start.y, end.z), Vector3f(end.x, start.y, end.z))

			render(Vector3f(start.x, start.y, start.z), Vector3f(start.x, start.y, end.z))
			render(Vector3f(end.x, start.y, start.z), Vector3f(end.x, start.y, end.z))
			render(Vector3f(end.x, end.y, start.z), Vector3f(end.x, end.y, end.z))
			render(Vector3f(start.x, end.y, start.z), Vector3f(start.x, end.y, end.z))
		}
		glCall { glEnable(GL_DEPTH_TEST) }
	}
}