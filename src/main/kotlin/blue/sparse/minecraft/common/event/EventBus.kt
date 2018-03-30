package blue.sparse.minecraft.common.event

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf

// I actually have not much of a clue what I'm doing, so...
class EventBus {

	private val listeners = ConcurrentHashMap<KClass<out Event>, MutableSet<Listener<*>>>()

	@Suppress("UNCHECKED_CAST")
	private fun <T : Event> getSpecificListeners(clazz: KClass<T>): MutableSet<Listener<T>> {
		return listeners.getOrPut(clazz) { ConcurrentSkipListSet() } as MutableSet<Listener<T>>
	}

	@Suppress("UNCHECKED_CAST")
	private fun getListeners(clazz: KClass<out Event>): Collection<Listener<Event>> {
		val superclasses = clazz.allSuperclasses.filter { it.isSubclassOf(Event::class) }
		return (superclasses.flatMap { getSpecificListeners(it as KClass<out Event>) } + getSpecificListeners(clazz)) as Collection<Listener<Event>>
	}

	fun <T : Event> register(clazz: KClass<T>, listener: Listener<T>): Boolean {
		return getSpecificListeners(clazz).add(listener)
	}

	inline fun <reified T : Event> register(listener: Listener<T>): Boolean {
		return register(T::class, listener)
	}

	fun <T : Event> register(clazz: KClass<T>, priority: Int = 0, listener: T.() -> Unit): Boolean {
		return register(clazz, Listener.LambdaListener(priority, listener))
	}

	inline fun <reified T : Event> register(priority: Int = 0, noinline listener: T.() -> Unit): Boolean {
		return register(T::class, Listener.LambdaListener(priority, listener))
	}

	fun post(event: Event) {
		for (listener in getListeners(event::class)) {
			try {
				listener.receiveEvent(event)
			} catch (e: Throwable) {
				System.err.println("Unhandled ${e::class.simpleName} in listener when posting event (${event.javaClass.name}) $event")
				e.printStackTrace()
			}
		}
	}

}