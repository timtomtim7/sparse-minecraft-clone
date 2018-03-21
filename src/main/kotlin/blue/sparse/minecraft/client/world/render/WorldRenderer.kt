package blue.sparse.minecraft.client.world.render

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.ints.Vector2i
import blue.sparse.math.vectors.ints.Vector3i
import blue.sparse.minecraft.client.MinecraftClient
import blue.sparse.minecraft.client.TextureAtlas
import blue.sparse.minecraft.client.entity.proxy.ClientEntityTypeProxy
import blue.sparse.minecraft.client.player.ClientPlayer
import blue.sparse.minecraft.client.world.proxy.ClientChunkProxy
import blue.sparse.minecraft.client.world.proxy.ClientWorldProxy
import blue.sparse.minecraft.common.world.*

class WorldRenderer(val world: World) {

	val renderDistance = PlayerChunks(ClientPlayer, 4, 2)

	fun render(delta: Float) {
		val camera = MinecraftClient.proxy.camera

		val sky = (world.proxy as ClientWorldProxy).sky
		sky.render(camera, delta)

		chunkShader.bind {
			uniforms["uLightDirection"] = sky.sun.direction
			uniforms["uViewProj"] = camera.viewProjectionMatrix
			atlas.texture.bind(0)
			uniforms["uTexture"] = 0

			var generate = 1
			fun get(pos: Vector3i): Chunk? {
				val chunk = world.getChunk(pos.x, pos.y, pos.z)
				if(chunk != null || generate <= 0)
					return chunk

				generate--
				return world.getOrGenerateChunk(pos.x, pos.y, pos.z)
			}

			for(chunkPosition in renderDistance) {
				val chunk = get(chunkPosition) ?: continue
//				chunk.debugBoundingBox()
				uniforms["uModel"] = Matrix4f.translation(chunk.worldBlockPosition.toFloatVector())
				val proxy = chunk.proxy as ClientChunkProxy

				if(proxy.shouldGenerateModel && proxy.canGenerateModel) {
					proxy.generateOfflineModel()
				}

				proxy.model?.render()/* ?: chunk.debugBoundingBox()*/
			}
		}

		for (entity in world.entities) {
			val proxy = entity.type.proxy as ClientEntityTypeProxy
			proxy.render(entity, camera, delta)
		}
	}

	companion object {
		val atlas = TextureAtlas(Vector2i(1024, 512))

		val chunkShader = ShaderProgram(
				Asset["minecraft/shaders/blocks.fs"],
				Asset["minecraft/shaders/blocks.vs"]
		)

	}

}