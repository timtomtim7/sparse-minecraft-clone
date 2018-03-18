package blue.sparse.minecraft.client.world.render

import blue.sparse.engine.render.resource.model.VertexLayout
import blue.sparse.math.vectors.bytes.Vector3b
import blue.sparse.math.vectors.floats.Vector2f
import blue.sparse.math.vectors.shorts.Vector3s
import blue.sparse.minecraft.common.world.Chunk

class OfflineChunkModel(chunk: Chunk) {

	companion object {
		private val layout = VertexLayout().apply {
			add<Vector3s>() // aPosition
			add<Vector2f>() // aTexCoord
			add<Vector3b>() // aColor
		}
	}


}