package blue.sparse.minecraft.common.util

import blue.sparse.math.vectors.ints.Vector3i

infix fun Vector3i.shl(bits: Int) = Vector3i(x shl bits, y shl bits, z shl bits)
infix fun Vector3i.shr(bits: Int) = Vector3i(x shr bits, y shr bits, z shr bits)
infix fun Vector3i.ushr(bits: Int) = Vector3i(x ushr bits, y ushr bits, z ushr bits)
infix fun Vector3i.and(bits: Int) = Vector3i(x and bits, y and bits, z and bits)
infix fun Vector3i.or(bits: Int) = Vector3i(x or bits, y or bits, z or bits)