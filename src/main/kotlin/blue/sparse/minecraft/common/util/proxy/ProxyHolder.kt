package blue.sparse.minecraft.common.util.proxy

interface ProxyHolder<out T: Proxy> {
	val proxy: T
}