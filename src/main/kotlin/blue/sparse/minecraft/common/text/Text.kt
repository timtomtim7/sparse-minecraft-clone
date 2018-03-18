package blue.sparse.minecraft.common.text

import blue.sparse.config.Config
import blue.sparse.config.write.BasicConfigWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

sealed class Text {

	var color: Int = 0xFFFFFF

	var textColor: TextColor
		get() = TextColor[color]
		set(value) { color = value.color }

	var bold: Boolean = false
	var italic: Boolean = false
	var underline: Boolean = false
	var strikethrough: Boolean = false
	var obfusated: Boolean = false
	var shadow: Boolean = true

	var extra: List<Text>? = null

	abstract val content: String

	class Raw(override val content: String): Text() {

		override fun applyToConfig(config: Config) {
			config["text"] = content
		}

	}

	class Translatable(val translationKey: String, vararg val with: Text): Text() {

		override val content: String
			get() = translationKey //TODO: Translate

		override fun applyToConfig(config: Config) {
			config["translate"] = translationKey
			with.takeIf { it.isNotEmpty() }?.let {
				config["with"] = it.map { it.toConfig() }
			}
		}

	}

	class Icon(val iconPath: String): Text() {
		override val content: String
			get() = iconPath

		override fun applyToConfig(config: Config) {
			config["icon"] = iconPath
		}

//		enum class Type {
//			ITEM, BLOCK, GUI
//		}
	}

	fun last(): Text {
		return extra?.lastOrNull()?.last() ?: this
	}

	// ?!?!?!?!
	fun addExtra(text: Text) {
		if ((extra as? MutableList?)?.add(text) == null) {
			extra = extra.orEmpty() + text
		}
	}

	fun addExtra(extra: Collection<Text>) {
		if ((this.extra as? MutableList?)?.addAll(extra) == null) {
			this.extra = this.extra.orEmpty() + extra
		}
	}

	fun toConfig(): Config {

		val config = Config {
			if(color != 0xFFFFFF) {
				set("color", textColor.id)
				if(textColor.color != color)
					set("customColor", color)
			}

			if(bold) set("bold", bold)
			if(italic) set("italic", italic)
			if(underline) set("underline", underline)
			if(strikethrough) set("strikethrough", strikethrough)
			if(obfusated) set("obfusated", obfusated)
			if(!shadow) set("shadow", shadow)
			extra?.let { set("extra", it.map(Text::toConfig)) }

			applyToConfig(this)
		}

		return config
	}

	fun toJSON(): String {
		val bytes = ByteArrayOutputStream()
		toConfig().write(bytes, true, BasicConfigWriter.compactJSON)
		return bytes.toByteArray().toString(Charsets.UTF_8)
	}

	protected abstract fun applyToConfig(config: Config)

	companion object {
		fun parse(jsonString: String): Text {
			val json = Config(ByteArrayInputStream(jsonString.toByteArray()))

			if("translate" in json) {

//				return Translatable(json.getTypedOrNull<String>("translate")!!, json.getTypedOrNull<List<>>())

			}

			return Text.Raw("Unable to parse chat JSON.").apply { textColor = TextColor.LIGHT_RED }
		}

		fun create(vararg values: Any): Text {
			return create(values.iterator())
		}

		private fun create(iterator: Iterator<Any>): Text {
			var color = 0xFFFFFF
			var rawContent: String? = null
			var text: Text? = null

			val extra = ArrayList<Text>()
			val effects = ArrayList<TextEffect>()

			while(rawContent == null && text == null && iterator.hasNext()) {
				val value = iterator.next()

				when(value) {
					is Int -> color = value
					is TextColor -> color = value.color
					is TextEffect -> effects.add(value)
					is String -> rawContent = value
					is Text -> text = value
					else -> rawContent = value.toString()
				}
			}

			if(text != null) {
				if(iterator.hasNext())
					extra.add(create(iterator))
				text.addExtra(extra)
				return text
			}

			if(rawContent == null)
				rawContent = ""

			if(iterator.hasNext())
				extra.add(create(iterator))

			return Text.Raw(rawContent).apply {
				this.color = color
				this.extra = extra
				effects.forEach { it.applyTo(this) }
			}
		}
	}
}