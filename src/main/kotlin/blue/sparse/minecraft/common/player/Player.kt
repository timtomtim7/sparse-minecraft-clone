package blue.sparse.minecraft.common.player

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.impl.types.living.EntityTypePlayer
import blue.sparse.minecraft.common.inventory.Inventory
import blue.sparse.minecraft.common.inventory.types.InventoryTypePlayer
import blue.sparse.minecraft.common.player.gamemode.GameMode
import blue.sparse.minecraft.common.player.gamemode.GameModeSurvival
import blue.sparse.minecraft.common.world.World

open class Player(protected var entity: Entity<EntityTypePlayer>? = null) {

    val inventory = Inventory(InventoryTypePlayer)//.apply { /* LOAD FROM WHEREVER */ }
    var walkSpeed = 4.317f
    var sprintSpeed = walkSpeed * 1.3f
    var sneakSpeed = walkSpeed * 0.3f
    var gameMode: GameMode = GameModeSurvival
    var hunger: Int = 20
    var saturation: Float = 0f

    var isFlying: Boolean = false
    var isSprinting: Boolean = false
    var isSneaking: Boolean = false

    var canFly: Boolean = false

    fun sendMessage(vararg message: Any?) {
        TODO("send a message")
    }

    fun teleport(location: Vector3f, world: World) {
        entity?.position = location.clone()
    }

}