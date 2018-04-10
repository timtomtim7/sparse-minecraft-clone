package blue.sparse.minecraft.common.player

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.entity.Entity
import blue.sparse.minecraft.common.entity.impl.types.living.EntityTypePlayer
import blue.sparse.minecraft.common.inventory.Inventory
import blue.sparse.minecraft.common.inventory.impl.types.InventoryTypePlayer
import blue.sparse.minecraft.common.player.gamemode.GameMode
import blue.sparse.minecraft.common.player.gamemode.GameModeSurvival
import blue.sparse.minecraft.common.world.PlayerChunks
import blue.sparse.minecraft.common.world.World

abstract class Player(entity: Entity<EntityTypePlayer>? = null) {

	open var entity = entity
		protected set

	val inventory = Inventory(InventoryTypePlayer)

	var walkSpeed = 4.317f
	var sprintSpeed = walkSpeed * 1.3f
	var sneakSpeed = walkSpeed * 0.3f

	var gameMode: GameMode = GameModeSurvival
//	var hunger: Int = 20
//	var saturation: Float = 0f

	var canFly: Boolean = false
	var isFlying: Boolean = false
		get() = field && canFly

	var isSprinting: Boolean = false
		get() = field && !isSneaking

	var isSneaking: Boolean = false

	var renderDistance = PlayerChunks(this, 8, 4)

	open fun sendMessage(vararg message: Any?) {}

	open fun teleport(position: Vector3f, world: World) {
		entity?.run {
			remove()
			this.world = world
			this.position = position.clone()
			add()
		}
	}

}