package blue.sparse.minecraft.client.player

import blue.sparse.engine.window.Window
import blue.sparse.engine.window.input.Input
import blue.sparse.engine.window.input.Key
import blue.sparse.math.abs
import blue.sparse.math.vectors.floats.*
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.impl.types.living.EntityTypePlayer
import blue.sparse.minecraft.common.player.Player

object ClientPlayer : Player() {
    private val mouseSensitivity: Float = 0.17f
    private var lastMousePos = Vector2f(0f)

	public override var entity: Entity<EntityTypePlayer>? = null

    fun input(input: Input, delta: Float) {
        freeMove(input, delta)
		if (input.window.cursorMode == Window.CursorMode.DISABLED)
			freeLook(input)
    }

    private fun freeMove(input: Input, delta: Float) {
        val entity = entity ?: return

		if (input[Key.HOME].pressed) {
			entity.position = Vector3f(0f, 100f, 0f)
			return
		}

        val wasdMovement = Vector3f(0f)
        if (input[Key.W].held) wasdMovement += Vector3f(0f, 0f, 1f)
        if (input[Key.S].held) wasdMovement += Vector3f(0f, 0f, -1f)
        if (input[Key.D].held) wasdMovement += Vector3f(1f, 0f, 0f)
        if (input[Key.A].held) wasdMovement += Vector3f(-1f, 0f, 0f)

		if(input[Key.SPACE].held && abs(entity.velocity.y) < 0.01f) {
			entity.velocity.y = 12f
		}

//        if (input[Key.SPACE].pressed) wasdMovement += Vector3f(0f, 1f, 0f)

        isSprinting = input[Key.LEFT_CONTROL].held

        var speed = when {
            isSneaking -> sneakSpeed
            isSprinting -> sprintSpeed
            else -> walkSpeed
        } * delta * 6f

		if (wasdMovement.any { it != 0f }) {
            var rotated = normalize(normalize(wasdMovement) * entity.rotation)
			rotated.y = 0f
			rotated = normalize(rotated)

            if (input[Key.TAB].held)
				speed *= 2

            val movement = rotated * speed
            Minecraft.world.testBlockIntersections(entity.type.bounds, entity.position, movement)

//			fun calculateVelocity(a: Float, b: Float): Float {
//				if(b > 0 && a > b)
//					return a
//				if(b < 0 && a < b)
//					return a
//
//				return a + b
//			}
////
//			entity.velocity.assign(
//					calculateVelocity(entity.velocity.x, movement.x),
//					calculateVelocity(entity.velocity.y, movement.y),
//					calculateVelocity(entity.velocity.z, movement.z)
//			)

//			entity.velocity = Vector3f(
//					max(entity.velocity.x, movement.x),
//					max(entity.velocity.x, movement.x),
//					max(entity.velocity.x, movement.x)
//			)

			entity.velocity.plusAssign(movement)
        }
    }

    private fun freeLook(input: Input) {
        val entity = entity ?: return

        val mousePos = input.mousePosition
        val mouseDiff = mousePos - lastMousePos
        lastMousePos = mousePos

        val diff = (mouseDiff * mouseSensitivity) * 0.015f

        if (diff.x != 0f)
            entity.rotation.rotate(Axis.Y.vector3, diff.x)

        if (diff.y != 0f)
            entity.rotation.rotate(entity.rotation.left, diff.y)
    }

}