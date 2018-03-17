package blue.sparse.minecraft.client.util

import blue.sparse.engine.render.resource.model.*

object BlankModel : Model by blankModel

private val blankModel = run {
	val array = VertexArray(GeometryPrimitive.POINTS)
	val layout = VertexLayout()
	val buffer = VertexBuffer()

	layout.add<Byte>()
	buffer.add(0.toByte())

	array.add(buffer, layout)

	BasicModel(array)
}