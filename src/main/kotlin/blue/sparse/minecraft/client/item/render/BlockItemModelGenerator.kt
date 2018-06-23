package blue.sparse.minecraft.client.item.render

import blue.sparse.engine.render.resource.model.*
import blue.sparse.math.vectors.floats.*
import blue.sparse.math.vectors.ints.Vector2i
import java.util.ArrayList

object BlockItemModelGenerator {

	data class TexCoords(
			val front: Vector4f,
			val back: Vector4f,
			val top: Vector4f,
			val bottom: Vector4f,
			val left: Vector4f,
			val right: Vector4f
	) {
		constructor(all: Vector4f): this(all, all, all, all, all, all)
	}

	fun generateModel(texCoords: TexCoords, atlasSize: Vector2i): Model {
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

		val frontNormal = Vector3f(0f, 0f, -1f)
		val backNormal = Vector3f(0f, 0f, 1f)
		val topNormal = Vector3f(0f, 1f, 0f)
		val bottomNormal = Vector3f(0f, -1f, 0f)
		val rightNormal = Vector3f(1f, 0f, 0f)
		val leftNormal = Vector3f(-1f, 0f, 0f)

		//TODO: Texture coordinates are rotated on some faces
		//front
		indices(0, 1, 2, 0, 2, 3)
		buffer.add(Vector3f(-0.5f, -0.5f, -0.5f), frontNormal, texCoords.front.xw)
		buffer.add(Vector3f(-0.5f, +0.5f, -0.5f), frontNormal, texCoords.front.xy)
		buffer.add(Vector3f(+0.5f, +0.5f, -0.5f), frontNormal, texCoords.front.zy)
		buffer.add(Vector3f(+0.5f, -0.5f, -0.5f), frontNormal, texCoords.front.zw)

		//back
		indices(0, 2, 1, 0, 3, 2)
		buffer.add(Vector3f(-0.5f, -0.5f, 0.5f), backNormal, texCoords.back.xw)
		buffer.add(Vector3f(-0.5f, +0.5f, 0.5f), backNormal, texCoords.back.xy)
		buffer.add(Vector3f(+0.5f, +0.5f, 0.5f), backNormal, texCoords.back.zy)
		buffer.add(Vector3f(+0.5f, -0.5f, 0.5f), backNormal, texCoords.back.zw)

		//top
		indices(0, 1, 2, 0, 2, 3)
		buffer.add(Vector3f(-0.5f, +0.5f, -0.5f), topNormal, texCoords.top.xw)
		buffer.add(Vector3f(-0.5f, +0.5f, +0.5f), topNormal, texCoords.top.xy)
		buffer.add(Vector3f(+0.5f, +0.5f, +0.5f), topNormal, texCoords.top.zy)
		buffer.add(Vector3f(+0.5f, +0.5f, -0.5f), topNormal, texCoords.top.zw)

		//bottom
		indices(0, 2, 1, 0, 3, 2)
		buffer.add(Vector3f(-0.5f, -0.5f, -0.5f), bottomNormal, texCoords.bottom.xw)
		buffer.add(Vector3f(-0.5f, -0.5f, +0.5f), bottomNormal, texCoords.bottom.xy)
		buffer.add(Vector3f(+0.5f, -0.5f, +0.5f), bottomNormal, texCoords.bottom.zy)
		buffer.add(Vector3f(+0.5f, -0.5f, -0.5f), bottomNormal, texCoords.bottom.zw)

		//left
		indices(0, 1, 2, 0, 2, 3)
		buffer.add(Vector3f(-0.5f, -0.5f, -0.5f), leftNormal, texCoords.left.zw)
		buffer.add(Vector3f(-0.5f, -0.5f, +0.5f), leftNormal, texCoords.left.xw)
		buffer.add(Vector3f(-0.5f, +0.5f, +0.5f), leftNormal, texCoords.left.xy)
		buffer.add(Vector3f(-0.5f, +0.5f, -0.5f), leftNormal, texCoords.left.zy)

		//right
		indices(0, 2, 1, 0, 3, 2)
		buffer.add(Vector3f(+0.5f, -0.5f, -0.5f), rightNormal, texCoords.right.zw)
		buffer.add(Vector3f(+0.5f, -0.5f, +0.5f), rightNormal, texCoords.right.xw)
		buffer.add(Vector3f(+0.5f, +0.5f, +0.5f), rightNormal, texCoords.right.xy)
		buffer.add(Vector3f(+0.5f, +0.5f, -0.5f), rightNormal, texCoords.right.zy)

		array.add(buffer, layout)
		return array.setIndices(indices.toIntArray()).toModel()
	}
}