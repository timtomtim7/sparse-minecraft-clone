package blue.sparse.minecraft.client.util

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.render.resource.shader.ShaderProgram

object BlankShader {
	val shader = ShaderProgram(vertex = Asset["minecraft/shaders/nothing.vs"])
}