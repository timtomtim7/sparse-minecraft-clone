package blue.sparse.minecraft.client.world.render

import blue.sparse.engine.render.resource.model.*
import blue.sparse.math.vectors.floats.*
import blue.sparse.math.vectors.shorts.Vector3s
import blue.sparse.minecraft.client.block.proxy.ClientBlockTypeProxy
import blue.sparse.minecraft.common.world.Chunk
import blue.sparse.minecraft.common.world.chunkBlockToWorldBlock

class OfflineChunkModel(private val chunk: Chunk) {

	private val buffer = VertexBuffer()

	private fun isSolid(x: Int, y: Int, z: Int): Boolean {

		return chunk.world.getBlock(x, y, z)?.type != null

//		val rx = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.x, chunk.regionChunkPosition.x, x)
//		val ry = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.y, chunk.regionChunkPosition.y, y)
//		val rz = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.z, chunk.regionChunkPosition.z, z)



//		val index = Chunk.indexOfBlock(x, y, z)
//		if (index < 0 || index >= Chunk.VOLUME)
//			return false
//
//		return chunk.getBlockType(index)?.transparent != false
	}

	init {
		val rcx = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.x, chunk.regionChunkPosition.x, 0)
		val rcy = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.y, chunk.regionChunkPosition.y, 0)
		val rcz = chunkBlockToWorldBlock(chunk.region.worldRegionPosition.z, chunk.regionChunkPosition.z, 0)

		for (i in 0 until Chunk.VOLUME) {
			val x = Chunk.xFromIndex(i)
			val y = Chunk.yFromIndex(i)
			val z = Chunk.zFromIndex(i)

			val rx = x + rcx
			val ry = y + rcy
			val rz = z + rcz

			val blockType = chunk.getBlockType(x, y, z) ?: continue
			val proxy = (blockType.proxy as ClientBlockTypeProxy)

//			println("DEBUG $x $y $z")

			//z+ front
			//z- back
			//x+ right
			//x- left
			//y+ top
			//y- bottom

			val px = !isSolid(rx + 1, ry, rz)
			val py = !isSolid(rx, ry + 1, rz)
			val pz = !isSolid(rx, ry, rz + 1)
			val nx = !isSolid(rx - 1, ry, rz)
			val ny = !isSolid(rx, ry - 1, rz)
			val nz = !isSolid(rx, ry, rz - 1)

//			println("$px $py $pz $nx $ny $nz")

			val x0 = (x * 16).toShort()
			val y0 = (y * 16).toShort()
			val z0 = (z * 16).toShort()
			val x1 = (x * 16 + 16).toShort()
			val y1 = (y * 16 + 16).toShort()
			val z1 = (z * 16 + 16).toShort()

			if (px) {
				val texCoords = proxy.rightSprite.textureCoords
				buffer.add(Vector3s(x1, y0, z0), texCoords.xw, positiveX, Vector3f(1f)) // A
				buffer.add(Vector3s(x1, y1, z0), texCoords.xy, positiveX, Vector3f(1f)) // B
				buffer.add(Vector3s(x1, y1, z1), texCoords.zy, positiveX, Vector3f(1f)) // C

				buffer.add(Vector3s(x1, y0, z0), texCoords.xw, positiveX, Vector3f(1f)) // A
				buffer.add(Vector3s(x1, y1, z1), texCoords.zy, positiveX, Vector3f(1f)) // C
				buffer.add(Vector3s(x1, y0, z1), texCoords.zw, positiveX, Vector3f(1f)) // D
			}

			if (nx) {
				val texCoords = proxy.leftSprite.textureCoords
				buffer.add(Vector3s(x0, y0, z1), texCoords.xw, negativeX, Vector3f(1f)) // D
				buffer.add(Vector3s(x0, y1, z1), texCoords.xy, negativeX, Vector3f(1f)) // C
				buffer.add(Vector3s(x0, y1, z0), texCoords.zy, negativeX, Vector3f(1f)) // B

				buffer.add(Vector3s(x0, y0, z1), texCoords.xw, negativeX, Vector3f(1f)) // D
				buffer.add(Vector3s(x0, y1, z0), texCoords.zy, negativeX, Vector3f(1f)) // B
				buffer.add(Vector3s(x0, y0, z0), texCoords.zw, negativeX, Vector3f(1f)) // A
			}

			if (py) {
				val texCoords = proxy.topSprite.textureCoords
				buffer.add(Vector3s(x0, y1, z1), texCoords.zw, positiveY, Vector3f(1f)) // D
				buffer.add(Vector3s(x1, y1, z1), texCoords.zy, positiveY, Vector3f(1f)) // C
				buffer.add(Vector3s(x1, y1, z0), texCoords.xy, positiveY, Vector3f(1f)) // B

				buffer.add(Vector3s(x0, y1, z1), texCoords.zw, positiveY, Vector3f(1f)) // D
				buffer.add(Vector3s(x1, y1, z0), texCoords.xy, positiveY, Vector3f(1f)) // B
				buffer.add(Vector3s(x0, y1, z0), texCoords.xw, positiveY, Vector3f(1f)) // A
			}

			if (ny) {
				val texCoords = proxy.bottomSprite.textureCoords
				buffer.add(Vector3s(x0, y0, z0), texCoords.xw, negativeY, Vector3f(1f)) // A
				buffer.add(Vector3s(x1, y0, z0), texCoords.xy, negativeY, Vector3f(1f)) // B
				buffer.add(Vector3s(x1, y0, z1), texCoords.zy, negativeY, Vector3f(1f)) // C

				buffer.add(Vector3s(x0, y0, z0), texCoords.xw, negativeY, Vector3f(1f)) // A
				buffer.add(Vector3s(x1, y0, z1), texCoords.zy, negativeY, Vector3f(1f)) // C
				buffer.add(Vector3s(x0, y0, z1), texCoords.zw, negativeY, Vector3f(1f)) // D
			}

			if (pz) {
				val texCoords = proxy.frontSprite.textureCoords
				buffer.add(Vector3s(x0, y0, z1), texCoords.zw, positiveZ, Vector3f(1f)) // A
				buffer.add(Vector3s(x1, y0, z1), texCoords.xw, positiveZ, Vector3f(1f)) // B
				buffer.add(Vector3s(x1, y1, z1), texCoords.xy, positiveZ, Vector3f(1f)) // C

				buffer.add(Vector3s(x0, y0, z1), texCoords.zw, positiveZ, Vector3f(1f)) // A
				buffer.add(Vector3s(x1, y1, z1), texCoords.xy, positiveZ, Vector3f(1f)) // C
				buffer.add(Vector3s(x0, y1, z1), texCoords.zy, positiveZ, Vector3f(1f)) // D
			}

			if (nz) {
				val texCoords = proxy.backSprite.textureCoords
				buffer.add(Vector3s(x0, y1, z0), texCoords.xy, negativeZ, Vector3f(1f)) // D
				buffer.add(Vector3s(x1, y1, z0), texCoords.zy, negativeZ, Vector3f(1f)) // C
				buffer.add(Vector3s(x1, y0, z0), texCoords.zw, negativeZ, Vector3f(1f)) // B

				buffer.add(Vector3s(x0, y1, z0), texCoords.xy, negativeZ, Vector3f(1f)) // D
				buffer.add(Vector3s(x1, y0, z0), texCoords.zw, negativeZ, Vector3f(1f)) // B
				buffer.add(Vector3s(x0, y0, z0), texCoords.xw, negativeZ, Vector3f(1f)) // A
			}
		}
	}

	fun upload(): VertexArray {
		return VertexArray().apply { add(buffer, layout) }
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