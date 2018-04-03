package blue.sparse.minecraft.common.util

import kotlin.reflect.KProperty

operator fun <T> ThreadLocal<T>.getValue(thisRef: Any?, property: KProperty<*>): T = get()
operator fun <T> ThreadLocal<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

inline fun <T> threadLocal(crossinline supplier: () -> T): ThreadLocal<T>
{
	return ThreadLocal.withInitial { supplier() }
}