package blue.sparse.minecraft.common.util

import java.util.concurrent.ThreadLocalRandom

val random: ThreadLocalRandom
	get() = ThreadLocalRandom.current()

fun <T> List<T>.random(): T {
	return get(random.nextInt(size))
}

fun <T> Array<T>.random(): T {
	return get(random.nextInt(size))
}

fun <T> Collection<T>.random(): T {
	val target = random.nextInt(size)
	val iter = iterator()
	for(i in 0 until target - 1)
		iter.next()
	return iter.next()
}