package blue.sparse.minecraft.common.util

data class Identifier(val namespace: String, val name: String) {
	constructor(name: String): this("minecraft", name)

	override fun toString() = "$namespace:$name"


}