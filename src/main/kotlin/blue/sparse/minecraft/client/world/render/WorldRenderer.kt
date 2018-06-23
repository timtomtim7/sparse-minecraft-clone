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
import blue.sparse.minecraft.client.world.render.thread.ChunkModellingThread
import blue.sparse.minecraft.common.util.math.AABB
import blue.sparse.minecraft.common.world.World
import blue.sparse.minecraft.common.world.chunk.Chunk
import blue.sparse.minecraft.common.world.worldChunkToWorldBlock
import kotlin.coroutines.experimental.buildSequence

class WorldRenderer(val world: World) {

	private val modellingThread: ChunkModellingThread

	var visible = 0
		private set

	init {
		modellingThread = ChunkModellingThread(world, buildSequence {
			while (true) {
				val player = ClientPlayer
				val frustum = MinecraftClient.proxy.camera.viewProjectionMatrix

				val renderDistance = player.renderDistance
				val found = renderDistance.firstOrNull {
					val blockPos = worldChunkToWorldBlock(it).toFloatVector()
					if (!inFrustum(frustum, blockPos, Chunk.bounds))
						return@firstOrNull false

					val chunk = world.getChunk(it) ?: return@firstOrNull false

					val proxy = chunk.proxy as ClientChunkProxy

					proxy.canGenerateModel && proxy.shouldGenerateModel
				}
				yield(world.getChunk(found ?: continue) ?: continue)
			}
		})
		modellingThread.start()
	}

	fun render(delta: Float) {
		ClientChunkProxy.update()

		val camera = MinecraftClient.proxy.camera

		val worldProxy = world.proxy as ClientWorldProxy
		val sky = worldProxy.sky
		sky.render(camera, delta)

		val viewProjection = camera.viewProjectionMatrix

//		StateManager.blend = true
//		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

		chunkShader.bind {
			//			uniforms["uLightDirection"] = worldProxy.lightDirection
			uniforms["uAmbientLight"] = Vector3f(0.85f, 0.8f, 0.8f)
			uniforms["uViewProj"] = viewProjection
			atlas.texture.bind(0)
			uniforms["uTexture"] = 0

//			var generate = 1

			visible = 0
			for (chunkPosition in ClientPlayer.renderDistance) {
				val blockPos = worldChunkToWorldBlock(chunkPosition).toFloatVector()
				if (!inFrustum(viewProjection, blockPos, Chunk.bounds))
					continue

				val chunk = world.getChunk(chunkPosition.x, chunkPosition.y, chunkPosition.z) ?: continue
				visible++

				uniforms["uModel"] = Matrix4f.translation(chunk.worldBlockPosition.toFloatVector())
				val proxy = chunk.proxy as ClientChunkProxy

				proxy.uploadOfflineModel()
//				if(generate > 0 && proxy.uploadOfflineModel())
//					generate--

				proxy.model?.render()
			}
		}

//		StateManager.blend = false

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
		for (point in points) {
			if (point.x < -point.w) tests[0]++
			if (point.x > point.w) tests[1]++
			if (point.y < -point.w) tests[2]++
			if (point.y > point.w) tests[3]++
			if (point.z < -point.w) tests[4]++
			if (point.z > point.w) tests[5]++
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