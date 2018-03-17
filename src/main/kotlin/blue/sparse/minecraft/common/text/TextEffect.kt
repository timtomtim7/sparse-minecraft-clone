package blue.sparse.minecraft.common.text

sealed class TextEffect {
	abstract fun applyTo(text: Text)

	object Bold: TextEffect() {
		override fun applyTo(text: Text) {
			text.bold = true
		}
	}

	object Italic: TextEffect() {
		override fun applyTo(text: Text) {
			text.italic = true
		}
	}

	object Obfuscated: TextEffect() {
		override fun applyTo(text: Text) {
			text.obfusated = true
		}
	}

	object Strikethrough: TextEffect() {
		override fun applyTo(text: Text) {
			text.strikethrough = true
		}
	}

	object Underline: TextEffect() {
		override fun applyTo(text: Text) {
			text.underline = true
		}
	}

}