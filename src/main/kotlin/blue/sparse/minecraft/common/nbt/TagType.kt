package blue.sparse.minecraft.common.nbt

import com.google.common.primitives.Primitives
import java.io.DataInputStream
import java.io.DataOutputStream

sealed class TagType<T: Any>(val id: Int, val clazz: Class<T>)
{
	companion object
	{
		private val types: Set<TagType<out Any>> by lazy { hashSetOf(
				TagTypeByte, TagTypeShort, TagTypeInt, TagTypeLong,
				TagTypeFloat, TagTypeDouble,
				TagTypeByteArray, TagTypeIntArray, TagTypeList,
				TagTypeString,
				TagTypeCompound
		)}

		@Suppress("UNCHECKED_CAST")
		fun <T: Any> getType(obj: T) = types.find { Primitives.wrap(it.clazz).isAssignableFrom(obj.javaClass) } as? TagType<T>

		fun <T: Any> getTypeOrError(obj: T?): TagType<T>
		{
			if(obj == null) throw IllegalArgumentException("Cannot search for type of null")
			return getType(obj) ?: throw IllegalArgumentException("Could not find type for ${obj::class.java.name}")
		}

		internal fun getTypeById(id: Int) = types.find { it.id == id }
		internal fun getTypeByIdOrError(id: Int) = getTypeById(id) ?: throw IllegalArgumentException("No type matches the given id: $id")
	}

	open fun isSupported(obj: Any) = Primitives.wrap(clazz).isInstance(obj)
	abstract fun write(out: DataOutputStream, obj: T)
	abstract fun read(inp: DataInputStream): T
	open fun toJSON(obj: T): String = obj.toString()

	protected open fun toFormattedJSON(obj: T, spacing: Int) = toJSON(obj)
	fun toFormattedJSON(obj: T): String = toFormattedJSON(obj, 1)

	object TagTypeByte: TagType<Byte>(1, Byte::class.java)
	{
		override fun write(out: DataOutputStream, obj: Byte) = out.writeByte(obj.toInt())
		override fun read(inp: DataInputStream): Byte = inp.readByte()
		override fun toJSON(obj: Byte) = "${obj}b"
	}

	object TagTypeShort: TagType<Short>(2, Short::class.java)
	{
		override fun write(out: DataOutputStream, obj: Short) = out.writeShort(obj.toInt())
		override fun read(inp: DataInputStream): Short = inp.readShort()
		override fun toJSON(obj: Short) = "${obj}s"
	}

	object TagTypeInt: TagType<Int>(3, Int::class.java)
	{
		override fun write(out: DataOutputStream, obj: Int) = out.writeInt(obj)
		override fun read(inp: DataInputStream): Int = inp.readInt()
	}

	object TagTypeLong: TagType<Long>(4, Long::class.java)
	{
		override fun write(out: DataOutputStream, obj: Long) = out.writeLong(obj)
		override fun read(inp: DataInputStream): Long = inp.readLong()
		override fun toJSON(obj: Long) = "${obj}l"
	}

	object TagTypeFloat: TagType<Float>(5, Float::class.java)
	{
		override fun write(out: DataOutputStream, obj: Float) = out.writeFloat(obj)
		override fun read(inp: DataInputStream): Float = inp.readFloat()
		override fun toJSON(obj: Float) = "${obj}f"
	}

	object TagTypeDouble: TagType<Double>(6, Double::class.java)
	{
		override fun write(out: DataOutputStream, obj: Double) = out.writeDouble(obj)
		override fun read(inp: DataInputStream): Double = inp.readDouble()
	}

	object TagTypeByteArray: TagType<ByteArray>(7, ByteArray::class.java)
	{
		override fun write(out: DataOutputStream, obj: ByteArray)
		{
			out.writeInt(obj.size)
			out.write(obj)
		}

		override fun read(inp: DataInputStream): ByteArray
		{
			val bytes = ByteArray(inp.readInt())
			inp.read(bytes)
			return bytes
		}

		override fun toJSON(obj: ByteArray): String
		{
			return obj.joinToString(", ", "[", "]", -1) { "${it}b" }
		}
	}

	object TagTypeString: TagType<String>(8, String::class.java)
	{
		override fun write(out: DataOutputStream, obj: String) = out.writeUTF(obj)
		override fun read(inp: DataInputStream): String = inp.readUTF()
		override fun toJSON(obj: String) = "\"$obj\""
	}

	object TagTypeList: TagType<List<*>>(9, List::class.java)
	{
		override fun isSupported(obj: Any): Boolean
		{
			if(obj !is List<*>) return false
			if(obj.isEmpty()) return true

			val nnList = obj.requireNoNulls()
			val type = getType(nnList.first()) ?: return false

			return nnList.none { getType(it) != type }
		}

		override fun write(out: DataOutputStream, obj: List<*>)
		{
			if(obj.isEmpty())
			{
				out.write(0)
				out.writeInt(0)
				return
			}

			val nnList = obj.requireNoNulls()
			val type = getTypeOrError(nnList.first())

			out.write(type.id)
			out.writeInt(nnList.size)
			nnList.forEach { type.write(out, it) }
		}

		override fun read(inp: DataInputStream): List<*>
		{
			val id = inp.readByte().toInt()
			val length = inp.readInt()

			val result = ArrayList<Any>(length)
			if(length == 0) return result

			val type = getTypeByIdOrError(id)
			for(i in 1..length) result.add(type.read(inp))

			return result
		}

		override fun toJSON(obj: List<*>): String
		{
			return obj.requireNoNulls().joinToString(", ", "[", "]", -1) { getTypeOrError(it).toJSON(it) }
		}

		override fun toFormattedJSON(obj: List<*>, spacing: Int): String
		{
			val spaces = "  ".repeat(spacing)
			val spaces1 = "  ".repeat(spacing-1)
			return obj.requireNoNulls().joinToString(",\n$spaces", "[\n$spaces", "\n$spaces1]", -1) {
				getTypeOrError(it).toFormattedJSON(it, spacing+1)
			}
		}
	}

	object TagTypeCompound: TagType<Compound>(10, Compound::class.java)
	{
		override fun write(out: DataOutputStream, obj: Compound)
		{
			obj.forEach { name, value ->
				val type = getTypeOrError(value)
				out.write(type.id)
				out.writeUTF(name)
				type.write(out, value)
			}
			out.write(0)
		}

		override fun read(inp: DataInputStream): Compound
		{
			val result = Compound()
			while(true)
			{
				val id = inp.readByte().toInt()
				if(id == 0) break
				result.put(inp.readUTF(), getTypeByIdOrError(id).read(inp))
			}
			return result
		}

		override fun toJSON(obj: Compound): String
		{
			return obj.entries.joinToString(", ", "{", "}", -1) {
				"\"${it.key}\": ${getTypeOrError(it.value).toJSON(it.value)}"
			}
		}

		override fun toFormattedJSON(obj: Compound, spacing: Int): String
		{
			val spaces = "  ".repeat(spacing)
			val spaces1 = "  ".repeat(spacing-1)
			return obj.entries.joinToString(",\n$spaces", "{\n$spaces", "\n$spaces1}", -1) {
				"\"${it.key}\": ${getTypeOrError(it.value).toFormattedJSON(it.value, spacing+1)}"
			}
		}
	}

	object TagTypeIntArray: TagType<IntArray>(11, IntArray::class.java)
	{
		override fun write(out: DataOutputStream, obj: IntArray)
		{
			out.writeInt(obj.size)
			obj.forEach(out::writeInt)
		}

		override fun read(inp: DataInputStream) = IntArray(inp.readInt(), { inp.readInt() })

		override fun toJSON(obj: IntArray) = obj.joinToString(", ", "[", "]")
	}
}