package blue.sparse.minecraft.common.text

import blue.sparse.math.vectors.floats.lengthSquared
import blue.sparse.math.vectors.floats.vectorFromIntRGB

class TextColor private constructor(val color: Int, val id: String, val char: Char) {

	init {
		colors[char] = this
	}

	companion object {
		private val colors = HashMap<Char, TextColor>()

		val BLACK 			= TextColor(0x000000, "black", '0')
		val BLUE 			= TextColor(0x0000AA, "dark_blue", '1')
		val GREEN 			= TextColor(0x00AA00, "dark_green", '2')
		val AQUA 			= TextColor(0x00AAAA, "dark_aqua", '3')
		val RED 			= TextColor(0xAA0000, "dark_red", '4')
		val PURPLE 			= TextColor(0xAA00AA, "dark_purple", '5')
		val ORANGE 			= TextColor(0xFFAA00, "gold", '6')
		val LIGHT_GRAY 		= TextColor(0xAAAAAA, "gray", '7')
		val DARK_GRAY 		= TextColor(0x555555, "dark_gray", '8')
		val LIGHT_BLUE 		= TextColor(0x5555FF, "blue", '9')
		val LIGHT_GREEN 	= TextColor(0x55FF55, "green", 'a')
		val CYAN 			= TextColor(0x55FFFF, "aqua", 'b')
		val LIGHT_RED 		= TextColor(0xFF5555, "red", 'c')
		val PINK 			= TextColor(0xFF55FF, "light_purple", 'd')
		val LIGHT_YELLOW 	= TextColor(0xFFFF55, "yellow", 'e')
		val WHITE 			= TextColor(0xFFFFFF, "white", 'f')

		fun values() = colors.values.toList()

		operator fun get(char: Char): TextColor? {
			return colors[char]
		}

		operator fun get(color: Int): TextColor {
			val vecColor = color.vectorFromIntRGB()

			return colors.values.minBy {
				lengthSquared(it.color.vectorFromIntRGB() - vecColor)
			}!!
		}
	}

}
