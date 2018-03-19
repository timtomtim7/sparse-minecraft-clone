package blue.sparse.minecraft.common.item

import blue.sparse.minecraft.common.nbt.Compound

data class Item<out T : ItemType>(val type: T, var data: Compound? = null) {
	var color: Int
		get() {
			return data?.compound("display")?.int("color") ?: 0xFFFFFF
		}
		set(value) {
			val display = ensureNBT().compound("display") ?: Compound()
			display.int("color", value)
			data!!.compound("display", display)
		}

	var enchantColor: Int
		get() {
			return data?.compound("display")?.int("enchantColor") ?: 0x3F003F
		}
		set(value) {
			val display = ensureNBT().compound("display") ?: Compound()
			display.int("enchantColor", value)
			data!!.compound("display", display)
		}

	val enchanted: Boolean
		get() {
			return data?.list("ench") != null
		}

	inline fun editNBT(body: Compound.() -> Unit) {
		ensureNBT().apply(body)
	}

//	var scale: Float
//		get() {
//			return data?.compound("display")?.float("scale") ?: 1f
//		}
//		set(value) {
//			val display = ensureNBT().compound("display") ?: Compound()
//			display.float("scale", value)
//			data!!.compound("display", display)
//		}

	fun ensureNBT(): Compound {
		data = data ?: Compound()
		return data!!
	}

	fun deepCopy() = Item(type, data?.deepCopy())
}