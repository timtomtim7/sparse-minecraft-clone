package blue.sparse.minecraft.common.util

interface Proxy {
	interface Client: Proxy
	interface Server: Proxy
}