package blue.sparse.minecraft.common.world.chunk

import com.google.common.collect.HashBiMap
import java.util.concurrent.ConcurrentLinkedQueue

class Palette<T> {

	private val palette = HashBiMap.create<T, Short>()
	private var recycledIDs: ConcurrentLinkedQueue<Short>? = null
	private var nextID: Short = 0

	@Synchronized
	private fun getNextID(): Short {
		val recycled = recycledIDs
		if (recycled != null) {
			val result = recycled.poll()
			if (result != null)
				return result
			recycledIDs = null
		}

		return nextID++
	}

	@Synchronized
	fun remove(id: Short) {
		var recycled = recycledIDs
		if (recycled == null) {
			recycled = ConcurrentLinkedQueue()
			recycledIDs = recycled
		}

		recycled.add(id)
	}

	fun remove(element: T) {
		remove(this[element])
	}

	operator fun get(element: T): Short {
		val id = palette[element]
		if (id != null)
			return id

		val next = getNextID()
		palette[element] = next
		return next
	}

	operator fun get(id: Short): T? {
		return palette.inverse()[id]
	}
}