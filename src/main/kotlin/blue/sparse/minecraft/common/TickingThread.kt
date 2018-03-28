package blue.sparse.minecraft.common

import blue.sparse.math.util.DeltaTimer
import blue.sparse.math.util.FrequencyTimer

class TickingThread(name: String, val targetTickRate: Double, private val onTick: (Float) -> Unit) : Thread(name) {

	private val tickTimer = FrequencyTimer(1.0 / targetTickRate)

	var ticking: Boolean = false
		private set

	var tickRate: Double = targetTickRate
		private set

	var partialTicks: Double = 0.0
		private set
//		get() = tickTimer.count

	override fun run() {
		var tickCounter = 0.0
		val secondTimer = FrequencyTimer(1.0)

		val deltaTimer = DeltaTimer()
		ticking = true
		while (ticking) {
			if (!tickTimer.use()) {
				partialTicks = tickTimer.count
				continue
			}

			partialTicks = tickTimer.count

			val delta = deltaTimer.deltaFloat()
			onTick(delta)

			tickCounter++
			tickRate = tickCounter / secondTimer.count

			if (secondTimer.use()) {
				val ms = 1000.0 / tickRate

				System.out.printf("TPS: %2.3f | MS: %5.2f%n", tickRate, ms)
				tickCounter -= tickRate
			}
		}
	}

	fun stopTicking() {
		ticking = false
	}

//	companion object {
//		const val TARGET_TICK_RATE = 20.0
//	}
}