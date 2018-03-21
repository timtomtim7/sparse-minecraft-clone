package blue.sparse.minecraft.common

import blue.sparse.math.util.DeltaTimer
import blue.sparse.math.util.FrequencyTimer

class TickingThread(name: String, private val onTick: (Float) -> Unit) : Thread(name) {

	var ticking: Boolean = false
		private set

	var tickRate: Double = TARGET_TICK_RATE
		private set

	override fun run() {
		var tickCounter = 0.0
		val tickTimer = FrequencyTimer(1.0 / TARGET_TICK_RATE)
		val secondTimer = FrequencyTimer(1.0)

		val deltaTimer = DeltaTimer()
		ticking = true
		while(ticking) {
			if(!tickTimer.use())
				continue

			val delta = deltaTimer.deltaFloat()
			onTick(delta)

			tickCounter++
			tickRate = tickCounter / secondTimer.count

			if(secondTimer.use()) {
				val ms = 1000.0 / tickRate

				System.out.printf("TPS: %2.3f | MS: %5.2f%n", tickRate, ms)
				tickCounter -= tickRate
			}
		}
	}

	fun stopTicking() {
		ticking = false
	}

	companion object {
		const val TARGET_TICK_RATE = 20.0
	}
}