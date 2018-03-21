package blue.sparse.minecraft.client.player

import blue.sparse.engine.window.input.Input
import blue.sparse.engine.window.input.Key
import blue.sparse.math.vectors.floats.*
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.player.Player

object ClientPlayer : Player() {
    private val mouseSensitivity: Float = 0.17f
    private var lastMousePos = Vector2f(0f)

    fun input(input: Input, delta: Float) {
        freeMove(input, delta)
        freeLook(input)
    }

    private fun freeMove(input: Input, delta: Float) {
        val entity = entity ?: return

        val wasdMovement = Vector3f(0f)
        if (input[Key.W].held) wasdMovement += Vector3f(0f, 0f, 1f)
        if (input[Key.S].held) wasdMovement += Vector3f(0f, 0f, -1f)
        if (input[Key.D].held) wasdMovement += Vector3f(1f, 0f, 0f)
        if (input[Key.A].held) wasdMovement += Vector3f(-1f, 0f, 0f)
        if (input[Key.SPACE].pressed) wasdMovement += Vector3f(0f, 1f, 0f)

        isSprinting = input[Key.LEFT_CONTROL].held

        var speed = when {
            isSneaking -> sneakSpeed
            isSprinting -> sprintSpeed
            else -> walkSpeed
        } * delta

        if (wasdMovement.any { it != 0f }) {
            val rotated = normalize((normalize(wasdMovement) * entity.rotation))

            if (input[Key.TAB].held) speed *= 2

            val movement = rotated * speed
            Minecraft.world.testBlockIntersections(entity.type.bounds, entity.position, movement)
            entity.position.plusAssign(movement)
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