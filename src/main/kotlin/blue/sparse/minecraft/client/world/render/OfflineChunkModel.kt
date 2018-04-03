package blue.sparse.minecraft.client.world.render

import blue.sparse.engine.render.resource.model.*
import blue.sparse.math.vectors.floats.*
import blue.sparse.math.vectors.shorts.Vector3s
import blue.sparse.minecraft.client.block.proxy.ClientBlockTypeProxy
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.util.math.BlockFace
import blue.sparse.minecraft.common.world.Chunk
import java.nio.ByteBuffer

class OfflineChunkModel(private val chunk: Chunk) {

//	private val buffer = VertexBuffer()
	private val buffer: ByteBuffer
	private val indices = ArrayList<Int>()

	val worldBlock = chunk.worldBlockPosition

//	private fun getRelativeBlock(x: Int, y: Int, z: Int): BlockView? {
//		if (x < 0 || x >= Chunk.SIZE || y < 0 || y >= Chunk.SIZE || z < 0 || z >= Chunk.SIZE)
//			return chunk.world[x + worldBlock.x, y + worldBlock.y, z + worldBlock.z]
//
//		return chunk[x, y, z]
//	}

	private fun getRelativeBlockType(x: Int, y: Int, z: Int): BlockType? {
		if (x < 0 || x >= Chunk.SIZE || y < 0 || y >= Chunk.SIZE || z < 0 || z >= Chunk.SIZE)
			return chunk.world[x + worldBlock.x, y + worldBlock.y, z + worldBlock.z]?.type

		return chunk.getType(x, y, z)
	}

	private fun isSolid(x: Int, y: Int, z: Int): Boolean {
		return getRelativeBlockType(x, y, z)?.transparent == false
	}



	init {
		val buffer =  VertexBuffer()

		fun addIndices(vararg ints: Int) {
			val index = buffer.size / layout.size
			for (i in ints)
				indices.add(i + index)
		}

		for (i in 0 until Chunk.VOLUME) {
			val x = Chunk.xFromIndex(i)
			val y = Chunk.yFromIndex(i)
			val z = Chunk.zFromIndex(i)

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

			if(!(px || py || pz || nx || ny || nz))
				continue

			val blockType = chunk.getType(x, y, z) ?: continue
			val proxy = (blockType.proxy as ClientBlockTypeProxy)

			val x0 = (x * 16).toShort()
			val y0 = (y * 16).toShort()
			val z0 = (z * 16).toShort()
			val x1 = (x * 16 + 16).toShort()
			val y1 = (y * 16 + 16).toShort()
			val z1 = (z * 16 + 16).toShort()

			val v000 = Vector3s(x0, y0, z0)
			val v001 = Vector3s(x0, y0, z1)
			val v010 = Vector3s(x0, y1, z0)
			val v011 = Vector3s(x0, y1, z1)
			val v100 = Vector3s(x1, y0, z0)
			val v101 = Vector3s(x1, y0, z1)
			val v110 = Vector3s(x1, y1, z0)
			val v111 = Vector3s(x1, y1, z1)

			val blockColor = proxy.getColor(
					chunk.world,
					x + worldBlock.x,
					y + worldBlock.y,
					z + worldBlock.z
			)

			if (px) {
				val ao = calculateAO(x, y, z, BlockFace.POSITIVE_X, BlockFace.POSITIVE_Y, BlockFace.POSITIVE_Z)
				val texCoords = proxy.rightSprite.textureCoords

				if(ao[D] + ao[B] > ao[A] + ao[C])
					addIndices(A, B, D, B, C, D)
				else
					addIndices(A, B, C, A, C, D)

				buffer.add(v100, texCoords.xw, positiveX, positiveXBrightness * ao[0], blockColor) // A
				buffer.add(v110, texCoords.xy, positiveX, positiveXBrightness * ao[1], blockColor) // B
				buffer.add(v111, texCoords.zy, positiveX, positiveXBrightness * ao[2], blockColor) // C
				buffer.add(v101, texCoords.zw, positiveX, positiveXBrightness * ao[3], blockColor) // D
			}

			if (nx) {
				val ao = calculateAO(x, y, z, BlockFace.NEGATIVE_X, BlockFace.POSITIVE_Y, BlockFace.POSITIVE_Z)
				val texCoords = proxy.leftSprite.textureCoords

				if(ao[A] + ao[C] > ao[B] + ao[D])
					addIndices(D, C, A, C, B, A)
				else
					addIndices(D, C, B, D, B, A)

				buffer.add(v000, texCoords.zw, negativeX, negativeXBrightness * ao[0], blockColor) // A
				buffer.add(v010, texCoords.zy, negativeX, negativeXBrightness * ao[1], blockColor) // B
				buffer.add(v011, texCoords.xy, negativeX, negativeXBrightness * ao[2], blockColor) // C
				buffer.add(v001, texCoords.xw, negativeX, negativeXBrightness * ao[3], blockColor) // D
			}

			if (py) {
				val ao = calculateAO(x, y, z, BlockFace.POSITIVE_Y, BlockFace.POSITIVE_X, BlockFace.POSITIVE_Z)
				val texCoords = proxy.topSprite.textureCoords

				if(ao[A] + ao[C] > ao[B] + ao[D])
					addIndices(D, C, A, C, B, A)
				else
					addIndices(D, C, B, D, B, A)

				buffer.add(v010, texCoords.xw, positiveY, positiveYBrightness * ao[0], blockColor) // A
				buffer.add(v110, texCoords.xy, positiveY, positiveYBrightness * ao[1], blockColor) // B
				buffer.add(v111, texCoords.zy, positiveY, positiveYBrightness * ao[2], blockColor) // C
				buffer.add(v011, texCoords.zw, positiveY, positiveYBrightness * ao[3], blockColor) // D
			}

			if (ny) {
				val ao = calculateAO(x, y, z, BlockFace.NEGATIVE_Y, BlockFace.POSITIVE_X, BlockFace.POSITIVE_Z)
				val texCoords = proxy.bottomSprite.textureCoords

				if(ao[D] + ao[B] > ao[A] + ao[C])
					addIndices(A, B, D, B, C, D)
				else
					addIndices(A, B, C, A, C, D)

				buffer.add(v000, texCoords.xw, negativeY, negativeYBrightness * ao[0], blockColor) // A
				buffer.add(v100, texCoords.xy, negativeY, negativeYBrightness * ao[1], blockColor) // B
				buffer.add(v101, texCoords.zy, negativeY, negativeYBrightness * ao[2], blockColor) // C
				buffer.add(v001, texCoords.zw, negativeY, negativeYBrightness * ao[3], blockColor) // D
			}

			if (pz) {
				val ao = calculateAO(x, y, z, BlockFace.POSITIVE_Z, BlockFace.NEGATIVE_X, BlockFace.POSITIVE_Y)
				val texCoords = proxy.frontSprite.textureCoords

				if(ao[D] + ao[B] > ao[A] + ao[C])
					addIndices(C, B, D, B, A, D)
				else
					addIndices(C, B, A, C, A, D)

				buffer.add(v101, texCoords.xw, positiveZ, positiveZBrightness * ao[0], blockColor) // A
				buffer.add(v001, texCoords.zw, positiveZ, positiveZBrightness * ao[1], blockColor) // B
				buffer.add(v011, texCoords.zy, positiveZ, positiveZBrightness * ao[2], blockColor) // C
				buffer.add(v111, texCoords.xy, positiveZ, positiveZBrightness * ao[3], blockColor) // D
			}

			if (nz) {
				val ao = calculateAO(x, y, z, BlockFace.NEGATIVE_Z, BlockFace.NEGATIVE_X, BlockFace.POSITIVE_Y)
				val texCoords = proxy.backSprite.textureCoords

				if(ao[A] + ao[C] > ao[B] + ao[D])
					addIndices(B, C, A, C, D, A)
				else
					addIndices(B, C, D, B, D, A)

				buffer.add(v100, texCoords.zw, negativeZ, negativeZBrightness * ao[0], blockColor) // A
				buffer.add(v000, texCoords.xw, negativeZ, negativeZBrightness * ao[1], blockColor) // B
				buffer.add(v010, texCoords.xy, negativeZ, negativeZBrightness * ao[2], blockColor) // C
				buffer.add(v110, texCoords.zy, negativeZ, negativeZBrightness * ao[3], blockColor) // D
			}
		}

		this.buffer = buffer.toByteBuffer()
	}

	fun upload(): IndexedModel {
		return IndexedModel(VertexArray().apply { add(buffer, layout) }, indices.toIntArray())
	}

	private fun calculateAO(x: Int, y: Int, z: Int, faceForward: BlockFace, faceA: BlockFace, faceB: BlockFace): Array<Float> {
		fun isShadowing(a: Int, b: Int): Boolean {
			return getRelativeBlockType(
					x + faceForward.x + a * faceA.x + b * faceB.x,
					y + faceForward.y + a * faceA.y + b * faceB.y,
					z + faceForward.z + a * faceA.z + b * faceB.z
			)?.transparent == false
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

		val value = 0.6f

		val result = Array(4) { 1f }
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
			add<Float>() // aBrightness
			add<Vector3f>() // aColor
			//add<Vector3f>() // aLighting
		}

		private val positiveX = Vector3f(1f, 0f, 0f)
		private val negativeX = Vector3f(-1f, 0f, 0f)
		private val positiveY = Vector3f(0f, 1f, 0f)
		private val negativeY = Vector3f(0f, -1f, 0f)
		private val positiveZ = Vector3f(0f, 0f, 1f)
		private val negativeZ = Vector3f(0f, 0f, -1f)

		private val positiveXBrightness = 0.6f
		private val negativeXBrightness = 0.6f
		private val positiveYBrightness = 1.0f
		private val negativeYBrightness = 0.5f
		private val positiveZBrightness = 0.8f
		private val negativeZBrightness = 0.8f

		const val A = 0
		const val B = 1
		const val C = 2
		const val D = 3
	}
}