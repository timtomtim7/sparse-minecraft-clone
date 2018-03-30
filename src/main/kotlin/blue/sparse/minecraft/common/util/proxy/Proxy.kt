package blue.sparse.minecraft.common.util.proxy

interface Proxy {
	interface Client: Proxy
	interface Server: Proxy
}