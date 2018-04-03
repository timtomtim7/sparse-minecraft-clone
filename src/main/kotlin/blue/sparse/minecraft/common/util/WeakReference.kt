package blue.sparse.minecraft.common.util

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

operator fun <T> WeakReference<T>.getValue(thisRef: Any?, property: KProperty<*>): T? = get()

fun <T> weak(value: T) = WeakReference(value)