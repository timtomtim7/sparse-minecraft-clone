package blue.sparse.minecraft.client.gui

import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.floats.*
import blue.sparse.minecraft.client.TextureAtlas
import blue.sparse.minecraft.client.item.proxy.ClientItemTypeProxy
import blue.sparse.minecraft.client.text.TextRenderer
import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.item.ItemStack
import blue.sparse.minecraft.common.text.Text

abstract class GUI {
	private val identity = Matrix4f.identity()

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

	open fun drawTexturedRectangle(
			sprite: String,
			x: Float, y: Float,
			sizeX: Float, sizeY: Float = sizeX,
			repeatX: Float = 1f, repeatY: Float = 1f,
			color: Long = 0xFFFFFFFF,
			guiPrefix: Boolean = true
	) {
		drawTexturedRectangle(
				manager.atlas.getOrAddSprite(if (guiPrefix) "minecraft/textures/gui/$sprite.png" else "$sprite.png"),
				x, y, sizeX, sizeY, repeatX, repeatY, color
		)
	}

	open fun drawTexturedRectangle(
			sprite: TextureAtlas.Sprite,
			x: Float, y: Float,
			sizeX: Float, sizeY: Float = sizeX,
			repeatX: Float = 1f, repeatY: Float = 1f,
			color: Long = 0xFFFFFFFF
	) {
		Rectangle.drawTexturedRectangle(
				Vector3f(x, y, 0f),
				Vector2f(sizeX, sizeY),
				color.toInt().vectorFromIntRGBA(),
				sprite,
				Vector4f(0f, 0f, repeatX, repeatY),
				identity,
				manager.projection
		)
	}

	open fun drawRectangle(x: Float, y: Float, sizeX: Float, sizeY: Float, color: Long = 0xFFFFFFFF) {
		Rectangle.drawRectangle(
				Vector3f(x, y, 0f),
				Vector2f(sizeX, sizeY),
				color.toInt().vectorFromIntRGBA(),
				identity,
				manager.projection
		)
	}

	open fun drawItem(item: Item<*>, amount: Int, x: Float, y: Float) {
		Rectangle.drawTexturedRectangle(
				Vector3f(x, y, 0f),
				Vector2f(16f),
				Vector4f(item.color.vectorFromIntRGB(), 1f),
				(item.type.proxy as ClientItemTypeProxy).sprite,
				Vector4f(0f, 0f, 1f, 1f),
				identity,
				manager.projection
		)
		if (amount > 1) {
			val textBegin = x + 16f - TextRenderer.stringWidth(amount.toString())
			drawString(amount.toString(), textBegin, y)
		}
	}

	fun drawStack(stack: ItemStack<*>, x: Float, y: Float) {
		drawItem(stack.item, stack.amount, x, y)
	}

//	fun drawRepeatingRectangle(sprite: String, x: Float, y: Float, sizeX: Float, sizeY: Float = sizeX, repeat)
}