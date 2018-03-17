package blue.sparse.minecraft.common.util

import blue.sparse.minecraft.common.Minecraft
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

class ProxyProvider<out T : Proxy>(private val abstract: KClass<T>, private val client: String, private val server: String, private vararg val args: Any?) {

	private lateinit var _value: T

	val value: T
		get() {
			if(this::_value.isInitialized)
				return _value

			init()
			return _value
		}

	private fun init() {
		val clazz = Class.forName(if (Minecraft.side == Minecraft.Side.CLIENT) client else server).kotlin

		if (!clazz.isSubclassOf(abstract))
			throw IllegalArgumentException("$clazz is not a subclass of $abstract")

		_value = if (clazz.objectInstance != null) {
			if(args.isNotEmpty())
				throw IllegalStateException("Arguments were provided for proxy constructor but proxy is an object")
			abstract.cast(clazz.objectInstance)
		}else{
			val companion = clazz.companionObject
			if(companion != null && companion.isSubclassOf(ProxyHolder::class)) {
				if(args.isNotEmpty())
					throw IllegalStateException("Arguments were provided for proxy constructor but proxy is handled by companion object")

				val holder = ProxyHolder::class.cast(clazz.companionObjectInstance)
				abstract.cast(holder.proxy)
			} else {
				abstract.cast(clazz.primaryConstructor!!.call(*args))
			}
		}
	}

	operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
		return value
	}

	companion object {
		inline operator fun <reified T : Proxy> invoke(client: String, server: String, vararg args: Any?): ProxyProvider<T> {
			return ProxyProvider(T::class, client, server, *args)
		}
	}
}