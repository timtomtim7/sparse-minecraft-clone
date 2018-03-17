package blue.sparse.minecraft.client.gui

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.util.MemoryUsage
import org.lwjgl.opengl.GL11.*

object TestGUI : GUI() {
//	val text = Text.create(
//			TextEffect.Bold, TextEffect.Italic, TextEffect.Obfuscated, "Hello, world!"
//	)

	override fun update(delta: Float) {}

	override fun render(delta: Float) {

		//TODO: None of this is nearly final! All of these will probably be split into individual components

		val hotbarLeft = (manager.right / 2f) - (182f / 2f)
		val hotbarRight = (manager.right / 2f) + (182f / 2f)
		drawRectangle(
				"widgets/hotbar",
				hotbarLeft,
				manager.bottom,
				182f, 22f
		)

		drawRectangle("widgets/hotbar_selected", hotbarLeft - 1f, -1f, 24f)

//		val exp = Math.sin(System.currentTimeMillis() / 500.0).toFloat() * 0.5f + 0.5f
		val exp = 0.5f
		drawRectangle("icons/exp_empty", hotbarLeft, 24f, 182f, 5f)
		drawRectangle("icons/exp_full", hotbarLeft, 24f, 182f * exp, 5f, exp)

		for(i in 0 until 10) {
			drawRectangle("icons/heart_black_outline", hotbarLeft + (i * 8), 30f, 9f)
			drawRectangle("icons/heart_full", hotbarLeft + (i * 8), 30f, 9f)
		}

		for(i in 0 until 10) {
			drawRectangle("icons/food_black_outline", hotbarRight - ((i+1) * 8) - 1, 30f, 9f)
			drawRectangle("icons/food_full", hotbarRight - ((i+1) * 8) - 1, 30f, 9f)
		}

		glCall { glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA) }
//		glCall { glBlendEquation(GL_FUNC_SUBTRACT) }
		drawRectangle("icons/crosshair", manager.right / 2f - 16f / 2f, manager.top / 2f - 16f / 2f, 16f)
//		glCall { glBlendEquation(GL_FUNC_ADD) }
		glCall { glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) }

		drawString(String.format("FPS: %.1f", SparseEngine.frameRate), 1f, manager.top - 9)
		drawString(String.format("MEM: %s", MemoryUsage.getMemoryUsedString()), 1f, manager.top - 18)

		val (x, y, z) = SparseEngine.game.camera.transform.translation
		drawString(String.format("POS: %.1f, %.1f, %.1f", x, y, z), 1f, manager.top - 27)
//		drawText(text, manager.right / 2f - manager.right / 4f, manager.top / 2f, 4f)
	}
}

