package blue.sparse.minecraft.client.entity.render

import blue.sparse.config.Config
import blue.sparse.engine.asset.Asset
import blue.sparse.engine.render.resource.Texture
import blue.sparse.engine.render.resource.bind
import blue.sparse.engine.render.resource.model.*
import blue.sparse.engine.render.resource.shader.ShaderProgram
import blue.sparse.math.FloatTransform
import blue.sparse.math.matrices.Matrix4f
import blue.sparse.math.vectors.floats.*
import blue.sparse.minecraft.client.world.proxy.ClientWorldProxy
import blue.sparse.minecraft.common.Minecraft

class EntityModel(val texture: Texture, val bones: List<Bone>) {

	private val model: IndexedModel

	init {
		model = upload()
	}

	fun delete() {
		model.delete()
		model.array.delete()
	}

	fun createPose(): Pose {
		return Pose(bones.map { it.name to FloatTransform() }.toMap())
	}

	fun render(pose: Pose, viewProjMatrix: Matrix4f, modelMatrix: Matrix4f) {
//		Debug.addTempLine(Vector3f(0f, -1f, 0f), Vector3f(0f, 4f, 0f), Vector3f(1f))
		shader.bind {
			texture.bind(0)
			uniforms["uTexture"] = 0

			uniforms["uViewProj"] = viewProjMatrix
			uniforms["uModel"] = modelMatrix

			for (i in bones.indices) {
				val bone = bones[i]

				val transform = Matrix4f.translation(bone.origin / 16f)
				transform *= pose[bone.name].matrix

				uniforms["uBoneTransforms[$i]"] = transform
			}
//			uniforms["uBoneTransforms[0]"] = Matrix4f.identity()
//			uniforms["uBoneTransforms[1]"] = Matrix4f.identity()
			uniforms["uLightDirection"] = (Minecraft.world.proxy as ClientWorldProxy).lightDirection
			model.render()
		}
	}

	private fun upload(): IndexedModel {
		val indices = ArrayList<Int>()

		val buffer = VertexBuffer()

		for (i in bones.indices) {
			val bone = bones[i]
			for (j in bone.cubes.indices) {
				uploadCube(buffer, indices, bone.cubes[j], i)
			}
		}

		val array = VertexArray()
		array.add(buffer, layout)

		return IndexedModel(array, indices.toIntArray())
	}

	private fun uploadCube(buffer: VertexBuffer, indices: MutableList<Int>, cube: Cube, boneIndex: Int) {
		val size = cube.size

		val x0 = cube.origin.x / 16f
		val y0 = cube.origin.y / 16f
		val z0 = cube.origin.z / 16f

		val x1 = x0 + size.x / 16f
		val y1 = y0 + size.y / 16f
		val z1 = z0 + size.z / 16f

		val uvMin = cube.uv.xy
		val uvMax = cube.uv.zw

		val textureWidth = (size.x * 2 + size.z * 2) / (uvMax.x - uvMin.x)
		val textureHeight = (size.y + size.z) / (uvMax.y - uvMin.y)

		val texelWidth = 1.0f / textureWidth
		val texelHeight = 1.0f / textureHeight

		val htX = size.x * texelWidth
		val htZ = size.z * texelWidth
		val vtY = size.y * texelHeight
		val vtZ = size.z * texelHeight

		val uvnx = Vector4f(0f, vtZ, htZ, vtZ + vtY)
		val uvpx = Vector4f(htZ + htX, vtZ, htZ + htX + htZ, vtZ + vtY)

		val uvny = Vector4f(htZ + htX, 0f, htZ + htX + htX, vtZ)
		val uvpy = Vector4f(htZ, 0f, htZ + htX, vtZ)

		val uvnz = Vector4f(htZ, vtZ, htZ + htX, vtZ + vtY)
		val uvpz = Vector4f(htZ + htX + htZ, htZ, htZ + htX + htZ + htX, vtZ + vtY)

		uvnx.x += uvMin.x; uvnx.y += uvMin.y; uvnx.z += uvMin.x; uvnx.w += uvMin.y
		uvpx.x += uvMin.x; uvpx.y += uvMin.y; uvpx.z += uvMin.x; uvpx.w += uvMin.y
		uvny.x += uvMin.x; uvny.y += uvMin.y; uvny.z += uvMin.x; uvny.w += uvMin.y
		uvpy.x += uvMin.x; uvpy.y += uvMin.y; uvpy.z += uvMin.x; uvpy.w += uvMin.y
		uvnz.x += uvMin.x; uvnz.y += uvMin.y; uvnz.z += uvMin.x; uvnz.w += uvMin.y
		uvpz.x += uvMin.x; uvpz.y += uvMin.y; uvpz.z += uvMin.x; uvpz.w += uvMin.y

		fun addIndices(vararg ints: Int) {
			val index = buffer.size / layout.size
			for (i in ints)
				indices.add(i + index)
		}

		// X+
		addIndices(0, 1, 2, 0, 2, 3)
		buffer.add(Vector3f(x1, y0, z0), uvpx.xw, positiveX, boneIndex) // A
		buffer.add(Vector3f(x1, y1, z0), uvpx.xy, positiveX, boneIndex) // B
		buffer.add(Vector3f(x1, y1, z1), uvpx.zy, positiveX, boneIndex) // C
		buffer.add(Vector3f(x1, y0, z1), uvpx.zw, positiveX, boneIndex) // D

		// X- dfghdf
		addIndices(0, 2, 1, 0, 3, 2)
		buffer.add(Vector3f(x0, y0, z0), uvnx.zw, negativeX, boneIndex) // A
		buffer.add(Vector3f(x0, y1, z0), uvnx.zy, negativeX, boneIndex) // B
		buffer.add(Vector3f(x0, y1, z1), uvnx.xy, negativeX, boneIndex) // C
		buffer.add(Vector3f(x0, y0, z1), uvnx.xw, negativeX, boneIndex) // D

		// Y+
		addIndices(0, 2, 1, 0, 3, 2)
		buffer.add(Vector3f(x0, y1, z0), uvpy.xw, positiveY, boneIndex) // A
		buffer.add(Vector3f(x1, y1, z0), uvpy.xy, positiveY, boneIndex) // B
		buffer.add(Vector3f(x1, y1, z1), uvpy.zy, positiveY, boneIndex) // C
		buffer.add(Vector3f(x0, y1, z1), uvpy.zw, positiveY, boneIndex) // D

		// Y-
		addIndices(0, 1, 2, 0, 2, 3)
		buffer.add(Vector3f(x0, y0, z0), uvny.xw, negativeY, boneIndex) // A
		buffer.add(Vector3f(x1, y0, z0), uvny.xy, negativeY, boneIndex) // B
		buffer.add(Vector3f(x1, y0, z1), uvny.zy, negativeY, boneIndex) // C
		buffer.add(Vector3f(x0, y0, z1), uvny.zw, negativeY, boneIndex) // D

		// Z+ dfg
		addIndices(0, 2, 1, 0, 3, 2)
		buffer.add(Vector3f(x0, y0, z1), uvpz.zw, positiveZ, boneIndex) // A
		buffer.add(Vector3f(x0, y1, z1), uvpz.zy, positiveZ, boneIndex) // B
		buffer.add(Vector3f(x1, y1, z1), uvpz.xy, positiveZ, boneIndex) // C
		buffer.add(Vector3f(x1, y0, z1), uvpz.xw, positiveZ, boneIndex) // D

		// Z-
		addIndices(0, 1, 2, 0, 2, 3)
		buffer.add(Vector3f(x0, y0, z0), uvnz.xw, negativeZ, boneIndex) // A
		buffer.add(Vector3f(x0, y1, z0), uvnz.xy, negativeZ, boneIndex) // B
		buffer.add(Vector3f(x1, y1, z0), uvnz.zy, negativeZ, boneIndex) // C
		buffer.add(Vector3f(x1, y0, z0), uvnz.zw, negativeZ, boneIndex) // D
	}

	data class Bone(
			val name: String,
			val origin: Vector3f,
			val cubes: List<Cube>
	)

	data class Cube(
			val origin: Vector3f,
			val size: Vector3f,
			val uv: Vector4f
	)

	companion object {
		private val layout = VertexLayout().apply {
			add<Vector3f>()
			add<Vector2f>()
			add<Vector3f>()
			add<Int>()
		}

		private val positiveX = Vector3f(1f, 0f, 0f)
		private val negativeX = Vector3f(-1f, 0f, 0f)
		private val positiveY = Vector3f(0f, 1f, 0f)
		private val negativeY = Vector3f(0f, -1f, 0f)
		private val positiveZ = Vector3f(0f, 0f, 1f)
		private val negativeZ = Vector3f(0f, 0f, -1f)

		private val shader = ShaderProgram(Asset["minecraft/shaders/entity/entity.fs"], Asset["minecraft/shaders/entity/entity.vs"])

		fun load(asset: Asset): EntityModel {
			return load(Config(asset.inputStream, true))
		}

		@Suppress("UNCHECKED_CAST")
		fun load(config: Config): EntityModel {
			val textureSection = config.section("texture")!!
			val texture = Texture(Asset[textureSection["asset"] as String]).apply {
				nearestFiltering()
				clampToEdge()
			}

			val textureWidth = textureSection["width"] as Int
			val textureHeight = textureSection["height"] as Int

			val texelWidth = 1.0f / textureWidth
			val texelHeight = 1.0f / textureHeight

			val bones = ArrayList<Bone>()
//			val configBones = config.getTypedOrNull<List<Map<String, *>>>("bones")!!
			val configBones = config["bones"] as List<Map<String, *>>

			for (configBone in configBones) {
				val name = configBone["name"] as String

				val boneOrigin = Vector3f(0f)
				val configBoneOrigin = configBone["origin"] as List<Number>
				for (i in configBoneOrigin.indices)
					boneOrigin[i] = configBoneOrigin[i].toFloat()

				val cubes = ArrayList<Cube>()

				val configCubes = configBone["cubes"] as List<Map<String, *>>
				for (configCube in configCubes) {
					val cubeOrigin = Vector3f(0f)
					val configCubeOrigin = configCube["origin"] as List<Number>
					for (i in configCubeOrigin.indices)
						cubeOrigin[i] = configCubeOrigin[i].toFloat()

					val cubeSize = Vector3f(0f)
					val configCubeSize = configCube["size"] as List<Number>
					for (i in configCubeSize.indices)
						cubeSize[i] = configCubeSize[i].toFloat()

					val cubeUV = Vector2f(0f)
					val configCubeUV = configCube["uv"] as List<Number>
					for (i in configCubeUV.indices)
						cubeUV[i] = configCubeUV[i].toFloat()

					val fullUV = Vector4f(
							cubeUV,
							cubeUV.x + cubeSize.x * 2 + cubeSize.z * 2,
							cubeUV.y + cubeSize.y + cubeSize.z
					)

					fullUV.x *= texelWidth
					fullUV.y *= texelHeight
					fullUV.z *= texelWidth
					fullUV.w *= texelHeight

					cubes.add(Cube(cubeOrigin, cubeSize, fullUV))
				}

				bones.add(Bone(name, boneOrigin, cubes))
			}

			return EntityModel(texture, bones)
		}
	}
}