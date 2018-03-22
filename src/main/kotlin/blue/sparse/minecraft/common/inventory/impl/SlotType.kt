package blue.sparse.minecraft.common.inventory.impl

import blue.sparse.minecraft.common.item.ItemStack
import blue.sparse.minecraft.common.item.ItemType
import kotlin.reflect.KClass

abstract class SlotType(open val maxStackSize: Int = 64) {

	open fun accepts(stack: ItemStack<*>?): Boolean {
//		return stack == null || stack.amount <= maxStackSize
		return true
	}

	object Default : SlotType()

	class Typed(val type: KClass<out ItemType>) : SlotType(1) {
		override fun accepts(stack: ItemStack<*>?): Boolean {
			return stack == null || type.isInstance(stack.item.type)
		}

		companion object {
			inline operator fun <reified T: ItemType> invoke() = Typed(T::class)
		}
	}
}