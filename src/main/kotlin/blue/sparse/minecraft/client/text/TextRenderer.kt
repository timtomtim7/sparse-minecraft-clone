package blue.sparse.minecraft.client.text

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.render.resource.Texture
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.floats.*
import blue.sparse.minecraft.client.gui.GUIManager
import blue.sparse.minecraft.client.gui.Rectangle
import blue.sparse.minecraft.client.util.BlankModel
import blue.sparse.minecraft.client.world.render.WorldRenderer
import blue.sparse.minecraft.common.text.Text
import blue.sparse.minecraft.common.util.random
import java.awt.image.BufferedImage
import kotlin.math.max

object TextRenderer {

//	private val imageCharacterOrder = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";

	private const val STRING_CHUNK_SIZE = 8

	private val characterData = HashMap<Char, CharacterData>()

//	private val model = run {
//		val array = VertexArray(GeometryPrimitive.POINTS)
//		val layout = VertexLayout()
//		val buffer = VertexBuffer()
//
//		layout.add<Byte>()
//		buffer.add(0.toByte())
//
//		array.add(buffer, layout)
//
//		BasicModel(array)
//	}

	private val shader = ShaderProgram(Asset["minecraft/shaders/textured_colored.fs"], Asset["minecraft/shaders/nothing.vs"], Asset["minecraft/shaders/gui/text.gs"])

	private val texture: Texture

	init {
		val textureAsset = Asset["minecraft/textures/font/ascii.png"]
		val image = textureAsset.readImage()
		texture = Texture(image)
		texture.nearestFiltering()
		texture.clampToEdge()

		loadCharacterData(image)
	}

	private fun loadCharacterData(image: BufferedImage) {
		val order = Asset["minecraft/font_atlas_order.txt"].readText()
		val charMaxWidth = image.width / 16
		val charMaxHeight = image.height / 16

		//The ascii image is 16 chars by 16 chars
		for (x in 0 until 16) {
			for (y in 0 until 16) {
				val i = y * 16 + x

				val char = order[i]

				val cx = x * charMaxWidth
				val cy = y * charMaxHeight

				var width = 0

				//Width is detected by the number of solid pixels, space doesn't have any solid pixels but the width shouldn't be 0
				if (char == ' ') {
					width = (charMaxWidth / 8) * 3
				} else {
					for (lx in 0 until charMaxWidth) {
						for (ly in 0 until charMaxHeight) {
							if (((image.getRGB(lx+cx, ly+cy) shr 24) and 0xFF) != 0)
								width = max(width, lx+1)
						}
					}
				}

				//The x and y of the texture coordinate vec4 are the lower coordinates, the z and w and the higher x and y
				val texCoords = Vector4f(
						cx.toFloat() / image.width.toFloat(),
						cy.toFloat() / image.height.toFloat(),
						(cx + width).toFloat() / image.width.toFloat(),
						(cy + charMaxHeight).toFloat() / image.height.toFloat()
				)

//				println("Texture Coordinates of $char: $texCoords")

				val fractionalWidth = width.toFloat() / charMaxWidth.toFloat()
				characterData[char] = TextRenderer.CharacterData(char, fractionalWidth, texCoords)
			}
		}
	}

	fun stringWidth(string: String): Float {
		return string.sumByDouble {characterData[it]?.width?.toDouble() ?: 0.0 }.toFloat() * 8f + string.length
	}

	fun textWidth(text: Text, includeExtra: Boolean): Float {
//		val width = stringWidth(text.content) + if(text.bold) text.content.length else 0
		val width = if(text is Text.Icon) 9f else {
			stringWidth(text.content) + if(text.bold) text.content.length else 0
		}

		if(includeExtra) {
			val extras = text.extra ?: return width
			return width + extras.sumByDouble { textWidth(it, true).toDouble() }.toFloat()
		}

		return width
	}

	fun obfuscate(string: String): String {
		return String(string.map { char ->
			val data = characterData[char] ?: characterData['?']!!
			val similar = characterData.values.filter { it.width == data.width }
			similar[random.nextInt(similar.size)].char
		}.toCharArray())
	}

	fun drawString(
			string: String,
			origin: Vector3f,
			color: Vector3f,
			shadow: Boolean,
			scale: Float,
			italic: Boolean,
			bold: Boolean,
			obfuscated: Boolean,
			modelMatrix: Matrix4f,
			viewProjectionMatrix: Matrix4f
	) {
		shader.bind {
			uniforms["uModel"] = modelMatrix
			uniforms["uViewProj"] = viewProjectionMatrix
			uniforms["uTexture"] = 0
			uniforms["uScale"] = scale
			uniforms["uItalic"] = if(italic) 1 else 0
			uniforms["uPadding"] = (if (bold) 2f else 1f) / 8f
			texture.bind(0)

			var offset = 0f
			string.chunked(STRING_CHUNK_SIZE).forEach {
				val section = if(obfuscated) obfuscate(it) else it

				fun drawChunk(offsetX: Float, offsetY: Float, offsetZ: Float, chunkColor: Vector3f = color) {
					val pos = origin + Vector3f(offsetX, offsetY, offsetZ)
					drawStringChunk(section, pos, chunkColor)
//					if(shadow) {
//						drawStringChunk(section, pos + Vector3f(scale, -scale, 0.001f), color * 0.25f)
//					}
				}

				if(shadow) {
					drawChunk(offset + scale, -scale, 0.001f, color * 0.25f)
					if(bold)
						drawChunk(offset + scale + scale, -scale, 0.001f, color * 0.25f)
				}

				drawChunk(offset, 0f, 0f)
				if(bold)
					drawChunk(offset + scale, 0f, 0f)


				offset += (stringWidth(section) + if(bold) section.length else 0) * scale
			}
		}
	}

	fun drawText(text: Text, origin: Vector3f, scale: Float, modelMatrix: Matrix4f, viewProjectionMatrix: Matrix4f) {
//		println("Draw text ${text.toJSON()} at $origin")
		if(text is Text.Icon)
			drawIcon(text.iconPath, origin, scale, modelMatrix, viewProjectionMatrix)
		else
			drawString(
					text.content,
					origin,
					text.color.vectorFromIntRGB(),
					text.shadow,
					scale,
					text.italic,
					text.bold,
					text.obfusated,
					modelMatrix,
					viewProjectionMatrix
			)

		val extras = text.extra ?: return
		var offset = textWidth(text, false) * scale

		for (extra in extras) {
			drawText(extra, origin + Vector3f(offset, 0f, 0f), scale, modelMatrix, viewProjectionMatrix)
			offset += textWidth(extra, true) * scale
		}
	}

	private fun drawIcon(path: String, origin: Vector3f, scale: Float, modelMatrix: Matrix4f, viewProjectionMatrix: Matrix4f) {
		val sprite = WorldRenderer.atlas["$path.png"] ?: GUIManager.atlas.getOrAddSprite("$path.png")

		Rectangle.drawTexturedRectangle(
				origin,
				Vector2f(9f) * scale,
				Vector4f(1f),
				sprite,
				Vector4f(0f, 0f, 1f, 1f),
				modelMatrix,
				viewProjectionMatrix
		)
	}

	private fun drawStringChunk(string: String, origin: Vector3f, color: Vector3f) {
		if (string.length > STRING_CHUNK_SIZE)
			throw IllegalArgumentException("String chunks should not be larger than $STRING_CHUNK_SIZE chars")

		val padded = string.padEnd(STRING_CHUNK_SIZE)
		val data = padded.map { characterData[it] ?: characterData['?']!! } //TODO: Something about unsupported characters

		shader.uniforms["uOrigin"] = origin
		shader.uniforms["uColor"] = color

		for ((index, datum) in data.withIndex()) {
			shader.uniforms["uTexCoords[$index]"] = datum.texCoords
			shader.uniforms["uWidths[$index]"] = datum.width
		}

		BlankModel.render()
	}

	private data class CharacterData(val char: Char, val width: Float, val texCoords: Vector4f)
}