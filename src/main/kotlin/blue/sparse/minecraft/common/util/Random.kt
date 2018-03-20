package blue.sparse.minecraft.common.util

import java.util.concurrent.ThreadLocalRandom

val random: ThreadLocalRandom
	get() = ThreadLocalRandom.current()

