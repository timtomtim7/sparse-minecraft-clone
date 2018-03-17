package blue.sparse.minecraft.client.item.render

import blue.sparse.engine.render.resource.model.*
import blue.sparse.math.vectors.floats.*
import blue.sparse.math.vectors.ints.*
import java.awt.image.BufferedImage

object ItemModelGenerator {

	private val normals = arrayOf(
			Vector2i(1, 0),
			Vector2i(-1, 0),
			Vector2i(0, 1),
			Vector2i(0, -1)
	)

	data class Edge(val begin: Vector2i, val end: Vector2i, val normal: Vector2i) {

		val axis = if (begin.x != end.x) Axis.X else Axis.Y

//		val left = if(axis == Axis.X) begin.x else begin.y
//		val right = if(axis == Axis.X) end.x else end.y

		init {
			if (begin == end)
				throw IllegalStateException("A point is not an edge.")
		}

		fun overlaps(edge: Edge): Boolean {
			if (axis != edge.axis)
				return false

			if (axis == Axis.X) {
				if (begin.y != edge.begin.y)
					return false

				return begin.x < edge.end.x && end.x > edge.begin.x
			} else {
				if (begin.x != edge.begin.x)
					return false

				return begin.y < edge.end.y && end.y > edge.begin.y
			}
//			return left < edge.right && right > edge.left
		}

		override fun toString() = "[${begin.x}, ${begin.y}] -> [${end.x}, ${end.y}]"
	}

	/**
	 * @return `true` if pixel at coordinates is solid
	 */
	private operator fun BufferedImage.get(x: Int, y: Int): Boolean {
		if (x < 0 || y < 0 || x >= width || y >= width)
			return false

		return ((getRGB(x, y) shr 24) and 0xFF) != 0
	}

	fun generateModel(image: BufferedImage, texCoords: Vector4f, atlasSize: Vector2i): Model {
		val array = VertexArray()
		val layout = VertexLayout()
		val buffer = VertexBuffer()
		val indices = ArrayList<Int>()

		operator fun ArrayList<Int>.invoke(vararg values: Int) {
			val offset = buffer.size / layout.size
			values.forEach { indices.add(it + offset) }
		}

		layout.add<Vector3f>()
		layout.add<Vector3f>()
		layout.add<Vector2f>()

		val minTexCoord = texCoords.xy
		val maxTexCoord = texCoords.zw
		val texCoordRange = maxTexCoord - minTexCoord
		val imageSize = Vector2f(image.width.toFloat(), image.height.toFloat())
		val texelSize = 1f / atlasSize.toFloatVector()
		val halfTexelSize = texelSize / 2f
//		halfTexelSize.y = -halfTexelSize.y

		val depth = 1f / 16f

		val front = Vector3f(0f, 0f, -1f)
		val back = Vector3f(0f, 0f, 1f)

		//front
		indices(0, 1, 2, 0, 2, 3)
		buffer.add(Vector3f(-0.5f, -0.5f, 0f), front, texCoords.xw)
		buffer.add(Vector3f(-0.5f, +0.5f, 0f), front, texCoords.xy)
		buffer.add(Vector3f(+0.5f, +0.5f, 0f), front, texCoords.zy)
		buffer.add(Vector3f(+0.5f, -0.5f, 0f), front, texCoords.zw)

		//back
		indices(0, 2, 1, 0, 3, 2)
		buffer.add(Vector3f(-0.5f, -0.5f, depth), back, texCoords.xw)
		buffer.add(Vector3f(-0.5f, +0.5f, depth), back, texCoords.xy)
		buffer.add(Vector3f(+0.5f, +0.5f, depth), back, texCoords.zy)
		buffer.add(Vector3f(+0.5f, -0.5f, depth), back, texCoords.zw)

		val edges = findEdges(image)
		for (edge in edges) {
			if (edge.normal.x == -1 || edge.normal.y == 1)
				indices(0, 2, 1, 0, 3, 2)
			else if (edge.normal.x == 1 || edge.normal.y == -1)
				indices(0, 1, 2, 0, 2, 3)

			val begin = edge.begin.toFloatVector() / imageSize
			val end = edge.end.toFloatVector() / imageSize

			val normal2D = edge.normal.toFloatVector()
			val texCoordOffset = minTexCoord - (halfTexelSize * normal2D)

			val aTexCoords = (begin * texCoordRange) + texCoordOffset
			val bTexCoords = (begin * texCoordRange) + texCoordOffset
			val cTexCoords = (end * texCoordRange) + texCoordOffset
			val dTexCoords = (end * texCoordRange) + texCoordOffset

			begin.y = 1f - begin.y
			end.y = 1f - end.y

			val a = Vector3f(begin - 0.5f, 0f)
			val b = Vector3f(begin - 0.5f, depth)
			val c = Vector3f(end - 0.5f, depth)
			val d = Vector3f(end - 0.5f, 0f)

			normal2D.y *= -1f
			val normal = Vector3f(normal2D, 0f)

			buffer.add(a, normal, aTexCoords)
			buffer.add(b, normal, bTexCoords)
			buffer.add(c, normal, cTexCoords)
			buffer.add(d, normal, dTexCoords)

			//abc acd
		}

		array.add(buffer, layout)
		return IndexedModel(array, indices.toIntArray())
	}


	fun findEdges(image: BufferedImage): List<Edge> {
		val edges = ArrayList<Edge>()

		for (x in 0 until image.width) {
			for (y in 0 until image.height) {

				//If the pixel here is blank, skip it
				if (!image[x, y])
					continue

				for (normal in normals) {
					if (!image[x + normal.x, y + normal.y]) {
						val begin = Vector2i(x, y)
						val end = traceLine(image, begin, 1 - abs(normal), normal)

						val offset = (normal + 1) / 2
						begin += offset
						end += offset

						val edge = Edge(begin, end, normal)

						//TODO: Detect before following edge?
						if (edges.any { it.overlaps(edge) })
							continue

						edges.add(edge)
					}
				}
			}
		}

		return edges
	}

	private fun traceLine(image: BufferedImage, begin: Vector2i, direction: Vector2i, normal: Vector2i): Vector2i {
		val position = begin.clone()

		while (true) {
			position += direction
			if (!image[position.x, position.y] || image[position.x + normal.x, position.y + normal.y])
				break
		}

		return position
	}
}