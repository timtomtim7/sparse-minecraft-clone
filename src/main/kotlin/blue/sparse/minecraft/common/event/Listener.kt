package blue.sparse.minecraft.common.event

abstract class Listener<in T: Event>(val priority: Int): Comparable<Listener<*>> {
	abstract fun receiveEvent(event: T)

	override operator fun compareTo(other: Listener<*>): Int {
		return priority.compareTo(other.priority)
	}

	class LambdaListener<in T: Event>(priority: Int, val receiver: (T) -> Unit): Listener<T>(priority) {
		override fun receiveEvent(event: T) {
			receiver(event)
		}
	}
}