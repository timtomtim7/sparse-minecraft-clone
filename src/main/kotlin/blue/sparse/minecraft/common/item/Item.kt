package blue.sparse.minecraft.common.item

import blue.sparse.minecraft.common.item.impl.types.ItemTypeDurable
import blue.sparse.minecraft.common.nbt.Compound

data class Item<T : ItemType>(val type: T, var data: Compound? = null) {
    var color: Int
        get() {
            return data?.compound("display")?.int("color") ?: 0xFFFFFF
        }
        set(value) {
            editNBT {
                val display = compound("display") ?: Compound()
                display.int("color", value)
                compound("display", display)
            }
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
        get() = data?.list("ench") != null

    inline fun editNBT(body: Compound.() -> Unit) {
        ensureNBT().apply(body)
    }

    fun ensureNBT(): Compound {
        data = data ?: Compound()
        return data!!
    }

    fun deepCopy() = Item(type, data?.deepCopy())

    @Suppress("UNCHECKED_CAST")
    inline fun <reified N : ItemType> safeCast(): Item<N>? {
        if (!N::class.isInstance(type))
            return null
        return this as Item<N>
    }

    inline fun <reified N : ItemType> typeCast(): Item<N> {
        return safeCast() ?: throw TypeCastException("${type::class.qualifiedName} is not a subclass of ${N::class.qualifiedName}")
    }
}

var Item<out ItemTypeDurable>.damage: Int
    get() = data?.int("Damage") ?: 0
    set(value) {
        editNBT { int("Damage", value) }
    }