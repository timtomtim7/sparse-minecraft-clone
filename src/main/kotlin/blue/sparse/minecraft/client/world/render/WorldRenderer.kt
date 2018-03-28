package blue.sparse.minecraft.client.world.render

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.vectors.floats.Vector4f
import blue.sparse.math.vectors.ints.Vector2i
import blue.sparse.minecraft.client.MinecraftClient
import blue.sparse.minecraft.client.TextureAtlas
import blue.sparse.minecraft.client.entity.proxy.ClientEntityTypeProxy
import blue.sparse.minecraft.client.player.ClientPlayer
import blue.sparse.minecraft.client.world.proxy.ClientChunkProxy
import blue.sparse.minecraft.client.world.proxy.ClientWorldProxy
import blue.sparse.minecraft.common.util.AABB
import blue.sparse.minecraft.common.world.*

class WorldRenderer(val world: World) {

	var visible = 0
		private set

	fun render(delta: Float) {
		val camera = MinecraftClient.proxy.camera

		val sky = (world.proxy as ClientWorldProxy).sky
		sky.render(camera, delta)

		val viewProjection = camera.viewProjectionMatrix

		chunkShader.bind {
			uniforms["uLightDirection"] = sky.sun.direction
			uniforms["uViewProj"] = viewProjection
			atlas.texture.bind(0)
			uniforms["uTexture"] = 0

			var generate = 1

			visible = 0
			for(chunkPosition in ClientPlayer.renderDistance) {
				val blockPos = worldChunkToWorldBlock(chunkPosition).toFloatVector()
				if(!inFrustum(viewProjection, blockPos, Chunk.bounds))
					continue

				val chunk = world.getChunk(chunkPosition.x, chunkPosition.y, chunkPosition.z) ?: continue
				visible++

				uniforms["uModel"] = Matrix4f.translation(chunk.worldBlockPosition.toFloatVector())
				val proxy = chunk.proxy as ClientChunkProxy

				if(generate > 0 && proxy.shouldGenerateModel && proxy.canGenerateModel) {
					generate--
					proxy.generateOfflineModel()
				}

				proxy.model?.render()
			}
		}

		for (entity in world.entities) {
			val proxy = entity.type.proxy as ClientEntityTypeProxy
			proxy.render(entity, camera, delta)
		}
	}

	private fun inFrustum(frustum: Matrix4f, position: Vector3f, bounds: AABB): Boolean {
		val min = bounds.min + position
		val max = bounds.max + position

		val points = arrayOf(
				frustum * Vector4f(min.x, min.y, min.z, 1f),
				frustum * Vector4f(min.x, min.y, max.z, 1f),
				frustum * Vector4f(min.x, max.y, min.z, 1f),
				frustum * Vector4f(min.x, max.y, max.z, 1f),
				frustum * Vector4f(max.x, min.y, min.z, 1f),
				frustum * Vector4f(max.x, min.y, max.z, 1f),
				frustum * Vector4f(max.x, max.y, min.z, 1f),
				frustum * Vector4f(max.x, max.y, max.z, 1f)
		)

		val tests = IntArray(6)
		for(point in points) {
			if(point.x < -point.w) tests[0]++
			if(point.x >  point.w) tests[1]++
			if(point.y < -point.w) tests[2]++
			if(point.y >  point.w) tests[3]++
			if(point.z < -point.w) tests[4]++
			if(point.z >  point.w) tests[5]++
		}

		return points.size !in tests
	}

	companion object {
		val atlas = TextureAtlas(Vector2i(1024, 512))

		val chunkShader = ShaderProgram(
				Asset["minecraft/shaders/world/blocks.fs"],
				Asset["minecraft/shaders/world/blocks.vs"]
		)

	}

}