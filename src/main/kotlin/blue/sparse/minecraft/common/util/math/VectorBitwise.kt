package blue.sparse.minecraft.common.util.math

import blue.sparse.math.vectors.ints.Vector3i

infix fun Vector3i.shl(bits: Int) = Vector3i(x shl bits, y shl bits, z shl bits)
infix fun Vector3i.shr(bits: Int) = Vector3i(x shr bits, y shr bits, z shr bits)
infix fun Vector3i.ushr(bits: Int) = Vector3i(x ushr bits, y ushr bits, z ushr bits)
infix fun Vector3i.and(bits: Int) = Vector3i(x and bits, y and bits, z and bits)
infix fun Vector3i.or(bits: Int) = Vector3i(x or bits, y or bits, z or bits)

infix fun Vector3i.shl(bits: Vector3i) = Vector3i(x shl bits.x, y shl bits.y, z shl bits.z)
infix fun Vector3i.shr(bits: Vector3i) = Vector3i(x shr bits.x, y shr bits.y, z shr bits.z)
infix fun Vector3i.ushr(bits: Vector3i) = Vector3i(x ushr bits.x, y ushr bits.y, z ushr bits.z)
infix fun Vector3i.and(bits: Vector3i) = Vector3i(x and bits.x, y and bits.y, z and bits.z)
infix fun Vector3i.or(bits: Vector3i) = Vector3i(x or bits.x, y or bits.y, z or bits.z)