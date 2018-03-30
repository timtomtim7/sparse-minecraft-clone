package blue.sparse.minecraft.client.world.render

import blue.sparse.engine.render.resource.model.*
import blue.sparse.math.vectors.floats.*
import blue.sparse.math.vectors.shorts.Vector3s
import blue.sparse.minecraft.client.block.proxy.ClientBlockTypeProxy
import blue.sparse.minecraft.common.util.math.BlockFace
import blue.sparse.minecraft.common.world.BlockView
import blue.sparse.minecraft.common.world.Chunk

class OfflineChunkModel(private val chunk: Chunk) {

	private val buffer = VertexBuffer()

	val worldBlock = chunk.worldBlockPosition

	private fun getRelativeBlock(x: Int, y: Int, z: Int): BlockView? {
		if (x < 0 || x >= Chunk.SIZE || y < 0 || y >= Chunk.SIZE || z < 0 || z >= Chunk.SIZE)
			return chunk.world[x + worldBlock.x, y + worldBlock.y, z + worldBlock.z]

		return chunk[x, y, z]
	}

	private fun isSolid(x: Int, y: Int, z: Int): Boolean {
		return getRelativeBlock(x, y, z)?.type != null
	}

	init {
		for (i in 0 until Chunk.VOLUME) {
			val x = Chunk.xFromIndex(i)
			val y = Chunk.yFromIndex(i)
			val z = Chunk.zFromIndex(i)

			val blockType = chunk.getBlockType(x, y, z) ?: continue
			val proxy = (blockType.proxy as ClientBlockTypeProxy)

			//z+ front
			//z- back
			//x+ right
			//x- left
			//y+ top
			//y- bottom

			val px = !isSolid(x + 1, y, z)
			val py = !isSolid(x, y + 1, z)
			val pz = !isSolid(x, y, z + 1)
			val nx = !isSolid(x - 1, y, z)
			val ny = !isSolid(x, y - 1, z)
			val nz = !isSolid(x, y, z - 1)

			val x0 = (x * 16).toShort()
			val y0 = (y * 16).toShort()
			val z0 = (z * 16).toShort()
			val x1 = (x * 16 + 16).toShort()
			val y1 = (y * 16 + 16).toShort()
			val z1 = (z * 16 + 16).toShort()

			if (px) {
				val ao = calculateAO(x, y, z, BlockFace.POSITIVE_X, BlockFace.POSITIVE_Y, BlockFace.POSITIVE_Z)

				val texCoords = proxy.rightSprite.textureCoords
				buffer.add(Vector3s(x1, y0, z0), texCoords.xw, positiveX, ao[0]) // A
				buffer.add(Vector3s(x1, y1, z0), texCoords.xy, positiveX, ao[1]) // B
				buffer.add(Vector3s(x1, y1, z1), texCoords.zy, positiveX, ao[2]) // C

				buffer.add(Vector3s(x1, y0, z0), texCoords.xw, positiveX, ao[0]) // A
				buffer.add(Vector3s(x1, y1, z1), texCoords.zy, positiveX, ao[2]) // C
				buffer.add(Vector3s(x1, y0, z1), texCoords.zw, positiveX, ao[3]) // D
			}

			if (nx) {
				val ao = calculateAO(x, y, z, BlockFace.NEGATIVE_X, BlockFace.POSITIVE_Y, BlockFace.POSITIVE_Z)

				val texCoords = proxy.leftSprite.textureCoords
				buffer.add(Vector3s(x0, y0, z1), texCoords.xw, negativeX, ao[3]) // D
				buffer.add(Vector3s(x0, y1, z1), texCoords.xy, negativeX, ao[2]) // C
				buffer.add(Vector3s(x0, y1, z0), texCoords.zy, negativeX, ao[1]) // B

				buffer.add(Vector3s(x0, y0, z1), texCoords.xw, negativeX, ao[3]) // D
				buffer.add(Vector3s(x0, y1, z0), texCoords.zy, negativeX, ao[1]) // B
				buffer.add(Vector3s(x0, y0, z0), texCoords.zw, negativeX, ao[0]) // A
			}

			if (py) {
				val ao = calculateAO(x, y, z, BlockFace.POSITIVE_Y, BlockFace.POSITIVE_X, BlockFace.POSITIVE_Z)

				val texCoords = proxy.topSprite.textureCoords
				buffer.add(Vector3s(x0, y1, z1), texCoords.zw, positiveY, ao[3]) // D
				buffer.add(Vector3s(x1, y1, z1), texCoords.zy, positiveY, ao[2]) // C
				buffer.add(Vector3s(x1, y1, z0), texCoords.xy, positiveY, ao[1]) // B

				buffer.add(Vector3s(x0, y1, z1), texCoords.zw, positiveY, ao[3]) // D
				buffer.add(Vector3s(x1, y1, z0), texCoords.xy, positiveY, ao[1]) // B
				buffer.add(Vector3s(x0, y1, z0), texCoords.xw, positiveY, ao[0]) // A
			}

			if (ny) {
				val ao = calculateAO(x, y, z, BlockFace.NEGATIVE_Y, BlockFace.POSITIVE_X, BlockFace.POSITIVE_Z)

				val texCoords = proxy.bottomSprite.textureCoords
				buffer.add(Vector3s(x0, y0, z0), texCoords.xw, negativeY, ao[0]) // A
				buffer.add(Vector3s(x1, y0, z0), texCoords.xy, negativeY, ao[1]) // B
				buffer.add(Vector3s(x1, y0, z1), texCoords.zy, negativeY, ao[2]) // C

				buffer.add(Vector3s(x0, y0, z0), texCoords.xw, negativeY, ao[0]) // A
				buffer.add(Vector3s(x1, y0, z1), texCoords.zy, negativeY, ao[2]) // C
				buffer.add(Vector3s(x0, y0, z1), texCoords.zw, negativeY, ao[3]) // D
			}

			if (pz) {
				val ao = calculateAO(x, y, z, BlockFace.POSITIVE_Z, BlockFace.NEGATIVE_X, BlockFace.POSITIVE_Y)

				val texCoords = proxy.frontSprite.textureCoords
				buffer.add(Vector3s(x0, y1, z1), texCoords.zy, positiveZ, ao[2]) // C
				buffer.add(Vector3s(x0, y0, z1), texCoords.zw, positiveZ, ao[1]) // B
				buffer.add(Vector3s(x1, y0, z1), texCoords.xw, positiveZ, ao[0]) // A

				buffer.add(Vector3s(x1, y1, z1), texCoords.xy, positiveZ, ao[3]) // D
				buffer.add(Vector3s(x0, y1, z1), texCoords.zy, positiveZ, ao[2]) // C
				buffer.add(Vector3s(x1, y0, z1), texCoords.xw, positiveZ, ao[0]) // A
			}

			if (nz) {
				val ao = calculateAO(x, y, z, BlockFace.NEGATIVE_Z, BlockFace.NEGATIVE_X, BlockFace.POSITIVE_Y)

				val texCoords = proxy.backSprite.textureCoords
				buffer.add(Vector3s(x0, y0, z0), texCoords.xw, negativeZ, ao[1]) // B
				buffer.add(Vector3s(x0, y1, z0), texCoords.xy, negativeZ, ao[2]) // C
				buffer.add(Vector3s(x1, y1, z0), texCoords.zy, negativeZ, ao[3]) // D

				buffer.add(Vector3s(x1, y0, z0), texCoords.zw, negativeZ, ao[0]) // A
				buffer.add(Vector3s(x0, y0, z0), texCoords.xw, negativeZ, ao[1]) // B
				buffer.add(Vector3s(x1, y1, z0), texCoords.zy, negativeZ, ao[3]) // D
			}
		}
	}

	fun upload(): VertexArray {
		return VertexArray().apply { add(buffer, layout) }
	}

	private fun calculateAO(x: Int, y: Int, z: Int, faceForward: BlockFace, faceA: BlockFace, faceB: BlockFace): Array<Vector3f> {
		fun isShadowing(a: Int, b: Int): Boolean {
			return getRelativeBlock(
					x + faceForward.x + a * faceA.x + b * faceB.x,
					y + faceForward.y + a * faceA.y + b * faceB.y,
					z + faceForward.z + a * faceA.z + b * faceB.z
			)?.type?.transparent == false
		}

		val blocks = arrayOf(
				isShadowing(-1, 0),
				isShadowing(-1, -1),
				isShadowing(0, -1),
				isShadowing(1, -1),
				isShadowing(1, 0),
				isShadowing(1, 1),
				isShadowing(0, 1),
				isShadowing(-1, 1)
		)

		val value = Vector3f(0.6f)

		val result = Array(4) { Vector3f(1f) }
		if (blocks[0] || blocks[1] || blocks[2])
			result[0] = value
		if (blocks[2] || blocks[3] || blocks[4])
			result[1] = value
		if (blocks[4] || blocks[5] || blocks[6])
			result[2] = value
		if (blocks[6] || blocks[7] || blocks[0])
			result[3] = value

		return result
	}

	companion object {
		private val layout = VertexLayout().apply {
			add<Vector3s>() // aPosition
			add<Vector2f>() // aTexCoord
			add<Vector3f>() // aNormal
			add<Vector3f>() // aColor
			//add<Vector3f>() // aLighting
		}

		private val positiveX = Vector3f(1f, 0f, 0f)
		private val negativeX = Vector3f(-1f, 0f, 0f)
		private val positiveY = Vector3f(0f, 1f, 0f)
		private val negativeY = Vector3f(0f, -1f, 0f)
		private val positiveZ = Vector3f(0f, 0f, 1f)
		private val negativeZ = Vector3f(0f, 0f, -1f)
	}


}