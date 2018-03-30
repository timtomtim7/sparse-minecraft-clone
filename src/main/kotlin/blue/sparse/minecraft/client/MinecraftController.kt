package blue.sparse.minecraft.client

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.render.camera.Camera
import blue.sparse.engine.render.camera.CameraController
import blue.sparse.engine.window.Window
import blue.sparse.engine.window.input.*
import blue.sparse.math.vectors.floats.*
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.entity.impl.types.EntityTypeItem
import blue.sparse.minecraft.common.util.math.AABB

class MinecraftController(camera: Camera, private val mouseSensitivity: Float = 0.17f, private val movementSpeed: Float = 6f) : CameraController(camera) {
	private var lastMousePos = Vector2f(0f)

	val bounds = AABB(Vector3f(-0.3f, -1.62f, -0.3f), Vector3f(0.3f, 1.8f - 1.62f, 0.3f))

	override fun update(delta: Float) {
		val window = SparseEngine.window
		val input = window.input

		if (window.cursorMode == Window.CursorMode.NORMAL && (input[Key.ESCAPE].pressed || input[MouseButton.LEFT].pressed)) {
			window.cursorMode = Window.CursorMode.DISABLED
			lastMousePos = input.mousePosition
		}

		if (window.cursorMode == Window.CursorMode.DISABLED && (input[Key.ESCAPE].pressed))
			window.cursorMode = Window.CursorMode.NORMAL

		if (window.cursorMode != Window.CursorMode.DISABLED) return

		freeLook(input)
		freeMove(delta, input)

		Minecraft.world.entities.filter { it.type == EntityTypeItem && it.timeSinceSpawned >= 0.5f }.filter {
			bounds.isIntersecting(camera.transform.translation, it.type.bounds, it.position)
		}.forEach {
			it.remove()
		}
	}

	private fun freeMove(delta: Float, input: Input) {
		val wasdMovement = Vector3f(0f)
//        val verticalMovement = Vector3f(0f)

		if (input[Key.W].held) wasdMovement += Vector3f(0f, 0f, 1f)
		if (input[Key.S].held) wasdMovement += Vector3f(0f, 0f, -1f)
		if (input[Key.D].held) wasdMovement += Vector3f(1f, 0f, 0f)
		if (input[Key.A].held) wasdMovement += Vector3f(-1f, 0f, 0f)
//        if (input[Key.SPACE].held) verticalMovement += Vector3f(0f, 1f, 0f)
		if (input[Key.SPACE].pressed) wasdMovement += Vector3f(0f, 1f, 0f)
//        if (input[Key.LEFT_SHIFT].held) verticalMovement += Vector3f(0f, -1f, 0f)
		if (input[Key.LEFT_SHIFT].held) wasdMovement += Vector3f(0f, -1f, 0f)

		var speed = movementSpeed * delta

		if (wasdMovement.any { it != 0f }) {
//            val rotated = normalize((normalize(wasdMovement) * camera.transform.rotation).apply { y = 0f })
			val rotated = normalize((normalize(wasdMovement) * camera.transform.rotation))

			if (input[Key.TAB].held) speed *= 2
//			if (input[Key.Q].held) speed *= 8

			val movement = rotated * speed
			Minecraft.world.testBlockIntersections(bounds, camera.transform.translation, movement)

			camera.transform.translate(movement)
		}

//        if (verticalMovement.any { it != 0f }) {
//            val verticalNormalized = normalize(verticalMovement)
//
//			val intersection = Minecraft.world.testBlockIntersections(bounds, camera.transform.translation, verticalNormalized)
//			camera.transform.translate((verticalNormalized * intersection) * speed)
//        }
	}

	private fun freeLook(input: Input) {
		val mousePos = input.mousePosition
		val mouseDiff = mousePos - lastMousePos
		lastMousePos = mousePos

		val diff = (mouseDiff * mouseSensitivity) * 0.015f

		if (diff.x != 0f)
			camera.transform.rotateRad(Axis.Y.vector3, diff.x)

		if (diff.y != 0f)
			camera.transform.rotateRad(camera.transform.rotation.left, diff.y)
	}
}
