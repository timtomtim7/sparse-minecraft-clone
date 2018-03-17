package blue.sparse.minecraft.common.item

data class ItemStack<T: ItemType>(val item: Item<T>, val amount: Int = 1) {
	constructor(type: T, amount: Int = 1): this(Item(type), amount)
}