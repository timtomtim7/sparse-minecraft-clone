package blue.sparse.minecraft.common.nbt.serialization

import blue.sparse.minecraft.common.nbt.Compound
import java.util.UUID
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
interface Serializer<T : Any> {

	fun fromCompound(compound: Compound): T
	fun toCompound(value: T): Compound

	companion object {
		private val serializers = HashMap<KClass<*>, Serializer<*>>()

		init {
			register(
					{ UUID(it.long("most")!!, it.long("least")!!) },
					{ Compound("most" to it.mostSignificantBits, "least" to it.leastSignificantBits) }
			)
		}

		fun <T : Any> register(clazz: KClass<T>, serializer: Serializer<T>) {
			serializers[clazz] = serializer
		}

		inline fun <reified T : Any> register(crossinline from: (Compound) -> T, crossinline to: (T) -> Compound) {
			register(T::class, object : Serializer<T> {
				override fun fromCompound(compound: Compound) = from(compound)

				override fun toCompound(value: T) = to(value)
			})
		}

		operator fun <T : Any> get(clazz: KClass<T>): Serializer<T>? {
			return serializers[clazz] as Serializer<T>?
		}

		inline fun <reified T : Any> get() = get(T::class)

		fun <T : Any> deserialize(clazz: KClass<T>, compound: Compound): T? {
			val serializer = Serializer[clazz]
					?: throw IllegalArgumentException("No serializer found for ${clazz.qualifiedName}")
			return serializer.fromCompound(compound)
		}

		inline fun <reified T : Any> deserialize(compound: Compound): T? {
			return deserialize(T::class, compound)
		}

		fun <T : Any> serialize(clazz: KClass<T>, value: T): Compound {
			val serializer = Serializer[clazz]
					?: throw IllegalArgumentException("No serializer found for ${clazz.qualifiedName}")
			return serializer.toCompound(value)
		}

		inline fun <reified T : Any> serialize(value: T): Compound {
			return serialize(T::class, value)
		}
	}
}