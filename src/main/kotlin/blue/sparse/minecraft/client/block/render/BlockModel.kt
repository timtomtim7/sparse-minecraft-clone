package blue.sparse.minecraft.client.block.render

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.floats.Vector4f
import blue.sparse.minecraft.common.util.math.BlockFace

class BlockModel {

	data class Cube(
			val origin: Vector3f,
			val size: Vector3f,
			val faceData: Map<BlockFace, FaceData>
	)

	data class FaceData(
			val visible: Boolean,
			val ao: Boolean,
			val cull: Boolean,
			val textures: List<TextureData>
	)

	data class TextureData(
			val asset: String,
			val uv: Vector4f,
			val colorID: String // ???
	)

}