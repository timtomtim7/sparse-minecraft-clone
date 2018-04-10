package blue.sparse.minecraft.common.world.chunk

class ChunkElementStorage<T>(defaultValue: T) {

	constructor(defaultValue: T, data: Array<T>): this(defaultValue) {
		for(i in data.indices)
			this[i] = data[i]
	}

	private val palette = Palette<T>()

	private var data: ShortArray? = null
	private var filled: Short = palette[defaultValue]

	fun fill(element: T) {
		data = null
		filled = palette[element]
	}

	operator fun get(index: Int): T {
		val data = data ?: return palette[filled]!!
		return palette[data[index]]!!
	}

	operator fun set(index: Int, value: T) {
		var data = data
		if(data == null) {
			data = ShortArray(Chunk.VOLUME)
			this.data = data
		}

		data[index] = palette[value]
	}

	operator fun get(x: Int, y: Int, z: Int): T {
		return get(Chunk.indexOfBlock(x, y, z))
	}

	operator fun set(x: Int, y: Int, z: Int, value: T) {
		set(Chunk.indexOfBlock(x, y, z), value)
	}
}