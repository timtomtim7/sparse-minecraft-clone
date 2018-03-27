package blue.sparse.minecraft.client.entity.render

import blue.sparse.math.abs
import kotlin.math.max
import kotlin.math.min

class Animation(val keyframes: List<KeyFrame>) {

	val duration: Float = keyframes.last().time

	fun getSurroundingKeyFrames(time: Float): Pair<KeyFrame, KeyFrame> {
//		val wrappedTime = wrap(time, 0f, duration)
		val wrappedTime = abs(time) % duration

		val firstIndex = keyframes.indexOfLast { it.time <= wrappedTime }
		val secondIndex = (firstIndex + 1) % keyframes.size

//		println("$firstIndex $secondIndex")
		return keyframes[firstIndex] to keyframes[secondIndex]

//		var previous: KeyFrame? = null
//		val last = keyframes.last()
//
//		for(frame in keyframes) {
//			if(frame.time >= wrappedTime) {
//				if(previous == null)
//					previous = last
//
//				return previous to frame
//			}
//			previous = frame
//		}
//
//		return last to last
	}

	fun applyToPose(target: Pose, time: Float) {
		val (first, second) = getSurroundingKeyFrames(time)

		val wrappedTime = abs(time) % duration
//		println(wrappedTime)

		val minTime = min(first.time, second.time)
		val maxTime = max(first.time, second.time)
		//wrap(time, minTime, maxTime)
		val timeBetween = maxTime - minTime

		val progress = (wrappedTime - minTime) / timeBetween
//		println("$timeBetween $progress")

//		val progress = abs(wrappedTime - maxTime) / timeBetween

//		first.pose.copyTo(target)

		first.pose.lerp(second.pose, target, progress)
//		println("Result: ${first != second} $target")
	}

	data class KeyFrame(val pose: Pose, val time: Float)
}
