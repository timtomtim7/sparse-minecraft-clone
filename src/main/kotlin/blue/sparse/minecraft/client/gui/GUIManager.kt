package blue.sparse.minecraft.client.gui

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.errors.glCall
import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.ints.Vector2i
import blue.sparse.minecraft.client.TextureAtlas
import org.lwjgl.opengl.GL11.*
import java.util.Stack

object GUIManager {
	private val stack = Stack<GUI>()

	lateinit var projection: Matrix4f// = Camera.orthographic(0f, 0f, 0f, 0f, 0f, 0f, null)
		private set

	val atlas = TextureAtlas(Vector2i(1024, 1024))

	var scale: Int = 2
		set(value) {
			field = value
			updateProjection()
		}

	val bottom = 0f
	val left = 0f
	var top = 0f
		private set
	var right = 0f
		private set

	init {
		updateProjection()
	}

	fun updateProjection() {
		val window = SparseEngine.window
		right = window.width.toFloat() / scale
		top = window.height.toFloat() / scale

		//TODO: Math library needs updating, these should not require division by 2
		projection = Matrix4f.orthographic(0f, right / 2, 0f, top / 2, 100f, -100f)
	}

	fun open(gui: GUI) {
		if (gui in stack)
			stack.remove(gui)
		stack.push(gui)
	}

	fun close(gui: GUI): Boolean {
		return stack.remove(gui)
	}

	fun update(delta: Float) {
		stack.peek()?.render(delta)
	}

	fun render(delta: Float) {
		glCall { glClear(GL_DEPTH_BUFFER_BIT) }
		glCall { glEnable(GL_BLEND) }
		glCall { glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) }
		depth(false)
		stack.peek()?.render(delta)
		depth(true)
		glCall { glDisable(GL_BLEND) }
	}

	fun depth(value: Boolean) {
		if(value) {
			glCall { glEnable(GL_DEPTH_TEST) }
		}else {
			glCall { glDisable(GL_DEPTH_TEST) }
		}
	}
}