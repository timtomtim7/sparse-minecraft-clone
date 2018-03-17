package blue.sparse.minecraft.client.gui

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.util.MemoryUsage

//import blue.sparse.minecraft.common.text.Text

object TestGUI : GUI {
//	val text = Text.create(
//			TextEffect.Bold, TextEffect.Italic, TextEffect.Obfuscated, "Hello, world!"
//	)

	override fun update(delta: Float) {}

	override fun render(delta: Float) {

		drawRectangle("heart_black_outline", 50f, 50f, 9f * 10f, 9f, 10f, 1f)
		drawRectangle("heart_full", 50f, 50f, 9f * 10f, 9f, 10f, 1f)


//		Rectangle.drawRectangle(
//				Vector3f(50f, 50f, 0f),
//				Vector2f(9f * 10f, 9f),
//				Vector4f(1f, 1f, 1f, 1f),
//				manager.atlas.getOrAddSprite("minecraft/textures/gui/icons/heart_full.png"),
//				Vector4f(0f, 0f, 10f, 1f),
//				Matrix4f.identity(),
//				manager.projection
//		)

		drawString(String.format("FPS: %.1f", SparseEngine.frameRate), 1f, manager.top - 9)
		drawString(String.format("MEM: %s", MemoryUsage.getMemoryUsedString()), 1f, manager.top - 18)
//
		val (x, y, z) = SparseEngine.game.camera.transform.translation
		drawString(String.format("POS: %.1f, %.1f, %.1f", x, y, z), 1f, manager.top - 27)
//		drawText(text, manager.right / 2f - manager.right / 4f, manager.top / 2f, 4f)
	}
}

