package blue.sparse.minecraft.common.world.chunk

import blue.sparse.math.clamp
import blue.sparse.math.vectors.floats.*
import blue.sparse.math.vectors.ints.Vector3i

class LightStorage {
	private val data = ShortArray(Chunk.VOLUME)

	internal fun getRaw(index: Int): Int {
		return data[index].toInt()
	}

	internal fun setRaw(index: Int, value: Int) {
		data[index] = value.toShort()
	}

	private fun transformFloat(value: Float): Int {
		return (clamp(value, 0f, 1f) * 15f).toInt()
	}

	fun getRed(index: Int): Int {
		return (getRaw(index) shr 4) and 0xF
	}

	fun getGreen(index: Int): Int {
		return (getRaw(index) shr 8) and 0xF
	}

	fun getBlue(index: Int): Int {
		return (getRaw(index) shr 12) and 0xF
	}

	fun getSun(index: Int): Int {
		return (getRaw(index)) and 0xF
	}



	fun setRed(index: Int, value: Int) {
		setRaw(index, (getRaw(index).inv().or(0xF shl 4)).inv().or(value.and(0xF).shl(4)))
	}

	fun setGreen(index: Int, value: Int) {
		setRaw(index, (getRaw(index).inv().or(0xF shl 8)).inv().or(value.and(0xF).shl(8)))
	}

	fun setBlue(index: Int, value: Int) {
		setRaw(index, (getRaw(index).inv().or(0xF shl 12)).inv().or(value.and(0xF).shl(12)))
	}

	fun setSun(index: Int, value: Int) {
		setRaw(index, (getRaw(index).inv().or(0xF)).inv().or(value.and(0xF)))
	}



	fun getRed(x: Int, y: Int, z: Int): Int {
		return getRed(Chunk.indexOfBlock(x, y, z))
	}

	fun getGreen(x: Int, y: Int, z: Int): Int {
		return getGreen(Chunk.indexOfBlock(x, y, z))
	}

	fun getBlue(x: Int, y: Int, z: Int): Int {
		return getBlue(Chunk.indexOfBlock(x, y, z))
	}

	fun getSun(x: Int, y: Int, z: Int): Int {
		return getSun(Chunk.indexOfBlock(x, y, z))
	}



	fun setRed(x: Int, y: Int, z: Int, value: Int) {
		setRed(Chunk.indexOfBlock(x, y, z), value)
	}

	fun setGreen(x: Int, y: Int, z: Int, value: Int) {
		setGreen(Chunk.indexOfBlock(x, y, z), value)
	}

	fun setBlue(x: Int, y: Int, z: Int, value: Int) {
		setBlue(Chunk.indexOfBlock(x, y, z), value)
	}

	fun setSun(x: Int, y: Int, z: Int, value: Int) {
		setSun(Chunk.indexOfBlock(x, y, z), value)
	}



	fun getRedFloat(x: Int, y: Int, z: Int): Float {
		return getRed(x, y, z) / 15f
	}

	fun getGreenFloat(x: Int, y: Int, z: Int): Float {
		return getGreen(x, y, z) / 15f
	}

	fun getBlueFloat(x: Int, y: Int, z: Int): Float {
		return getBlue(x, y, z) / 15f
	}

	fun getSunFloat(x: Int, y: Int, z: Int): Float {
		return getSun(x, y, z) / 15f
	}



	fun setRedFloat(x: Int, y: Int, z: Int, value: Float) {
		return setRed(x, y, z, transformFloat(value))
	}

	fun setGreenFloat(x: Int, y: Int, z: Int, value: Float) {
		return setGreen(x, y, z, transformFloat(value))
	}

	fun setBlueFloat(x: Int, y: Int, z: Int, value: Float) {
		return setBlue(x, y, z, transformFloat(value))
	}

	fun setSunFloat(x: Int, y: Int, z: Int, value: Float) {
		return setSun(x, y, z, transformFloat(value))
	}



	fun getRGBFloatVector(x: Int, y: Int, z: Int): Vector3f {
		return Vector3f(
				getRedFloat(x, y, z),
				getGreenFloat(x, y, z),
				getBlueFloat(x, y, z)
		)
	}

	fun getRGBIntVector(x: Int, y: Int, z: Int): Vector3i {
		return Vector3i(
				getRed(x, y, z),
				getGreen(x, y, z),
				getBlue(x, y, z)
		)
	}

	fun setRGBFloatVector(x: Int, y: Int, z: Int, rgb: Vector3f) {
		setRedFloat(x, y, z, rgb.r)
		setGreenFloat(x, y, z, rgb.g)
		setBlueFloat(x, y, z, rgb.b)
	}

	fun setRGBIntVector(x: Int, y: Int, z: Int, rgb: Vector3i) {
		setRed(x, y, z, rgb.x)
		setGreen(x, y, z, rgb.y)
		setBlue(x, y, z, rgb.z)
	}
}