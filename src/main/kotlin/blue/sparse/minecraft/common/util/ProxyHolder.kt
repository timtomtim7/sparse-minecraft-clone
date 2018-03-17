package blue.sparse.minecraft.common.util

interface ProxyHolder<out T: Proxy> {
	val proxy: T
}