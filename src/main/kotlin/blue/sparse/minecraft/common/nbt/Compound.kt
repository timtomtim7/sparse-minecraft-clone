package blue.sparse.minecraft.common.nbt

import blue.sparse.minecraft.common.nbt.serialization.Serializer
import java.io.*
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.reflect.KClass

class Compound private constructor(
		private val backingMap: MutableMap<String, Any>,
		privateConstructorMarker: Nothing?
) : MutableMap<String, Any> by backingMap, Cloneable {

	constructor() : this(ConcurrentHashMap<String, Any>(), null)

	constructor(map: Map<String, Any>) : this() {
		putAll(map)
	}

	constructor(pairs: Collection<Pair<String, Any>>) : this(pairs.toMap())
	constructor(vararg pairs: Pair<String, Any>) : this(pairs.toMap())

	operator fun String.invoke(obj: Any) = put(this, obj)
	operator fun String.invoke(body: Compound.() -> Unit) = put(this, Compound().apply(body))

	fun byte(name: String): Byte? = getOrNull(name)
	fun short(name: String): Short? = getOrNull(name)
	fun int(name: String): Int? = getOrNull(name)
	fun long(name: String): Long? = getOrNull(name)
	fun float(name: String): Float? = getOrNull(name)
	fun double(name: String): Double? = getOrNull(name)
	fun byteArray(name: String): ByteArray? = getOrNull(name)
	fun string(name: String): String? = getOrNull(name)
	fun list(name: String): List<Any>? = getOrNull(name)
	fun compound(name: String): Compound? = getOrNull(name)
	fun intArray(name: String): IntArray? = getOrNull(name)

	@Suppress("UNCHECKED_CAST")
	inline fun <reified T> typedList(name: String) = list(name) as List<T>?

	fun byte(name: String, value: Byte) = set(name, value)
	fun short(name: String, value: Short) = set(name, value)
	fun int(name: String, value: Int) = set(name, value)
	fun long(name: String, value: Long) = set(name, value)
	fun float(name: String, value: Float) = set(name, value)
	fun double(name: String, value: Double) = set(name, value)
	fun byteArray(name: String, value: ByteArray) = set(name, value)
	fun string(name: String, value: String) = set(name, value)
	fun list(name: String, value: List<Any>) = set(name, value)
	fun list(name: String, vararg values: Any) = set(name, values.toList())

	fun compound(name: String, value: Compound) = set(name, value)
	fun compound(name: String, map: Map<String, Any>) = set(name, Compound(map))
	fun compound(name: String, pairs: Collection<Pair<String, Any>>) = set(name, Compound(pairs))
	fun compound(name: String, vararg pairs: Pair<String, Any>) = set(name, Compound(pairs.toMap()))
	inline fun compound(name: String, crossinline body: Compound.() -> Unit) = set(name, Compound(body))
	fun intArray(name: String, value: IntArray) = set(name, value)

	fun <T : Any> serialized(clazz: KClass<T>): T? {
		val serializer = Serializer[clazz]
				?: throw IllegalArgumentException("No serializer found for ${clazz.qualifiedName}")
		return serializer.fromCompound(this)
	}

	inline fun <reified T : Any> serialized(): T? {
		return serialized(T::class)
	}

	fun <T : Any> serialized(clazz: KClass<T>, name: String): T? {
		val serializer = Serializer[clazz]
				?: throw IllegalArgumentException("No serializer found for ${clazz.qualifiedName}")
		return serializer.fromCompound(compound(name)!!)
	}

	inline fun <reified T : Any> serialized(name: String): T? {
		return serialized(T::class, name)
	}

	fun <T : Any> serialized(clazz: KClass<T>, name: String, value: T) {
		val serializer = Serializer[clazz]
				?: throw IllegalArgumentException("No serializer found for ${clazz.qualifiedName}")
		compound(name, serializer.toCompound(value))
	}

	inline fun <reified T : Any> serialized(name: String, value: T) {
		serialized(T::class, name, value)
	}

	fun <T : Any> serializedList(clazz: KClass<T>, name: String): List<T>? {
		return typedList<Compound>(name)?.map { it.serialized(clazz)!! }
	}

	inline fun <reified T : Any> serializedList(name: String): List<T>? {
		return serializedList(T::class, name)
	}

	override fun put(key: String, value: Any): Any? {
		val type = TagType.getType(value)
				?: throw IllegalArgumentException("NBT specification does not support values of type ${value::class.java.name}")
		if (!type.isSupported(value)) throw IllegalArgumentException("NBT ${type.javaClass.simpleName} does not support the value provided.")

		return backingMap.put(key, value)
	}

	override fun get(key: String) = backingMap.get(key)

	inline fun <reified T : Any> getOrNull(key: String) = get(key) as? T

	fun getType(name: String): TagType<out Any>? = backingMap[name]?.let { TagType.getType(it) }

	fun isType(name: String, clazz: Class<*>) = clazz.isInstance(backingMap[name])
	fun isType(name: String, type: TagType<out Any>) = getType(name) == type

	inline fun <reified T> isType(name: String) = isType(name, T::class.java)

	fun write(output: OutputStream, close: Boolean = false) {
		val dataOut = output as? DataOutputStream ?: DataOutputStream(output)
		dataOut.write(10)
		dataOut.write(0)
		dataOut.write(0)
		TagType.TagTypeCompound.write(dataOut, this)
		if (close) dataOut.close()
	}

	fun write(file: File) = write(file.outputStream(), true)

	fun writeGZIP(file: File) = write(GZIPOutputStream(file.outputStream()), true)

	fun toJSON() = TagType.TagTypeCompound.toJSON(this)
	fun toFormattedJSON() = TagType.TagTypeCompound.toFormattedJSON(this)

	companion object {
		fun read(input: InputStream, close: Boolean = false): Compound {
			val dataInput = input as? DataInputStream ?: DataInputStream(input)

			if (dataInput.read() != 10 || dataInput.read() != 0 || dataInput.read() != 0)
				throw IOException("File is not in NBT format.")

			val result = TagType.TagTypeCompound.read(dataInput)
			if (close) input.close()
			return result
		}

		fun read(file: File) = read(file.inputStream(), true)

		fun readGZIP(file: File) = read(GZIPInputStream(file.inputStream()), true)

		inline operator fun invoke(body: Compound.() -> Unit): Compound {
			val result = Compound()
			body(result)
			return result
		}
	}

	override fun clone(): Compound {
		val result = Compound()
		forEach { k, v -> result.put(k, (v as? Compound)?.clone() ?: v) }
		return result
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Compound) return false

		if (backingMap != other.backingMap) return false

		return true
	}

	override fun hashCode(): Int {
		return backingMap.hashCode()
	}
}