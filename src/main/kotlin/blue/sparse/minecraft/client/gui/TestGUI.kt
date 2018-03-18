package blue.sparse.minecraft.client.gui

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.util.MemoryUsage
import blue.sparse.math.wrap
import blue.sparse.minecraft.client.item.proxy.ClientItemTypeProxy
import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.text.Text
import org.lwjgl.opengl.GL11.*

object TestGUI : GUI() {
	val text = Text.create(Text.Icon("minecraft/textures/items/diamond_sword"), " Hello ", Text.Icon("minecraft/textures/items/diamond_sword"))

	private var selectedSlot: Int = 0

	init {
		println(text.toJSON())
	}

	override fun update(delta: Float) {
		selectedSlot -= SparseEngine.window.input.scrollDelta.toInt()
		selectedSlot = wrap(selectedSlot, 0, 8)
	}

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

		drawRectangle("widgets/hotbar_selected", hotbarLeft - 1f + (selectedSlot * 20f), -1f, 24f)

		drawRectangle((ItemType.diamondSword.proxy as ClientItemTypeProxy).sprite, hotbarLeft + 3f + 20f * 2, 3f, 16f, 16f)

//		val exp = Math.sin(System.currentTimeMillis() / 500.0).toFloat() * 0.5f + 0.5f
		val exp = 0.5f
		drawRectangle("icons/exp_empty", hotbarLeft, 24f, 182f, 5f)
		drawRectangle("icons/exp_full", hotbarLeft, 24f, 182f * exp, 5f, exp)

		for (i in 0 until 10) {
			drawRectangle("icons/heart_black_outline", hotbarLeft + (i * 8), 30f, 9f)
			if (i < 5) {
				drawRectangle("icons/heart_full", hotbarLeft + (i * 8), 30f, 9f)
			} else if (i == 5) {
				drawRectangle("icons/heart_half", hotbarLeft + (i * 8), 30f, 9f)
			}
		}

		for (i in 0 until 10) {
			drawRectangle("icons/food_black_outline", hotbarRight - ((i + 1) * 8) - 1, 30f, 9f)
			drawRectangle("icons/food_full", hotbarRight - ((i + 1) * 8) - 1, 30f, 9f)
		}

		glCall { glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA) }
		drawRectangle("icons/crosshair", manager.right / 2f - 16f / 2f, manager.top / 2f - 16f / 2f, 16f)
		glCall { glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) }

		/*

			<---------- EXT START --------->

		 */

		drawString(String.format("FPS: %.1f", SparseEngine.frameRate), 1f, manager.top - 9)
		drawString(String.format("MEM: %s", MemoryUsage.getMemoryUsedString()), 1f, manager.top - 18)

		val (x, y, z) = SparseEngine.game.camera.transform.translation
		drawString(String.format("POS: %.1f, %.1f, %.1f", x, y, z), 1f, manager.top - 27)

//		drawText(text, manager.right / 2f - manager.right / 4f, manager.top / 2f, 1f)
	}
}

