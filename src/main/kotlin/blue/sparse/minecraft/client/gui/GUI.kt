package blue.sparse.minecraft.client.gui

import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.floats.*
import blue.sparse.minecraft.client.text.TextRenderer
import blue.sparse.minecraft.common.text.Text

abstract class GUI {
	val manager: GUIManager get() = GUIManager

	abstract fun update(delta: Float)
	abstract fun render(delta: Float)

	open fun drawString(
			text: String,
			x: Float,
			y: Float,
			color: Int = 0xFFFFFF,
			shadow: Boolean = true,
			italic: Boolean = false,
			bold: Boolean = false,
			obfuscated: Boolean = false,
			scale: Float = 1f
	) {
		TextRenderer.drawString(
				text,
				Vector3f(x, y, 0f),
				color.vectorFromIntRGB(),
				shadow,
				scale,
				italic,
				bold,
				obfuscated,
				Matrix4f.identity(),
				manager.projection
		)
	}

	open fun drawText(text: Text, x: Float, y: Float, scale: Float = 1f) {
		TextRenderer.drawText(
				text,
				Vector3f(x, y, 0f),
				scale,
				Matrix4f.identity(),
				manager.projection
		)
	}

	open fun drawRectangle(
			sprite: String,
			x: Float, y: Float,
			sizeX: Float, sizeY: Float = sizeX,
			repeatX: Float = 1f, repeatY: Float = 1f,
			color: Long = 0xFFFFFFFF,
			guiPrefix: Boolean = true
	) {
		Rectangle.drawRectangle(
				Vector3f(x, y, 0f),
				Vector2f(sizeX, sizeY),
				color.toInt().vectorFromIntRGBA(),
				manager.atlas.getOrAddSprite(if(guiPrefix) "minecraft/textures/gui/$sprite.png" else "$sprite.png"),
				Vector4f(0f, 0f, repeatX, repeatY),
				Matrix4f.identity(),
				manager.projection
		)
	}

//	fun drawRepeatingRectangle(sprite: String, x: Float, y: Float, sizeX: Float, sizeY: Float = sizeX, repeat)
}