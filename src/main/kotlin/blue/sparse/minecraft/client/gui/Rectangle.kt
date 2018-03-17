package blue.sparse.minecraft.client.gui

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.floats.*
import blue.sparse.minecraft.client.TextureAtlas
import blue.sparse.minecraft.client.util.BlankModel

object Rectangle {

	private val repeatingShader = ShaderProgram(
			Asset["minecraft/shaders/repeating_colored.fs"],
			Asset["minecraft/shaders/nothing.vs"],
			Asset["minecraft/shaders/repeating_rectangle.gs"]
	)

	fun drawRectangle(
			position: Vector3f,
			size: Vector2f,
			color: Vector4f,
			sprite: TextureAtlas.Sprite,
			texCoords: Vector4f,
			modelMatrix: Matrix4f,
			viewProjectionMatrix: Matrix4f
	) {
		repeatingShader.bind {
			sprite.atlas.texture.bind(0)
			uniforms["uTexture"] = 0
			uniforms["uPosition"] = position
			uniforms["uSize"] = size
			uniforms["uTexCoordRange"] = sprite.textureCoords
			uniforms["uTexCoords"] = texCoords

			uniforms["uColor"] = color
			uniforms["uModel"] = modelMatrix
			uniforms["uViewProj"] = viewProjectionMatrix

			BlankModel.render()
		}

	}
}