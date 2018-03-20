package blue.sparse.minecraft.client.gui

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.util.MemoryUsage
import blue.sparse.engine.window.input.Key
import blue.sparse.math.clamp
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.wrap
import blue.sparse.minecraft.client.MinecraftClient
import blue.sparse.minecraft.client.text.TextRenderer
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.entity.impl.types.EntityTypeItem
import blue.sparse.minecraft.common.inventory.TestInventory
import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.item.ItemStack
import blue.sparse.minecraft.common.item.impl.ItemTypeApple
import blue.sparse.minecraft.common.item.impl.ItemTypeIronIngot
import blue.sparse.minecraft.common.text.Text
import org.lwjgl.opengl.GL11.*

object TestGUI : GUI() {
	private var selectedSlot: Int = 0

	data class ChatMessage(val text: Text, var timeRemaining: Float)

	private val chatMessages = ArrayList<ChatMessage>()

//	private val item = Item(ItemType.chainmailChestplate)

	fun sendMessage(message: Text) {
		chatMessages.add(0, ChatMessage(message, 10f))
	}

	fun sendMessage(message: String) {
		sendMessage(Text.create(message))
	}

	override fun update(delta: Float) {
		selectedSlot -= SparseEngine.window.input.scrollDelta.toInt()
		selectedSlot = wrap(selectedSlot, 0, 8)

		chatMessages.removeAll {
			it.timeRemaining -= delta
			it.timeRemaining <= 0f
		}

//		if (Math.random() <= 0.01) {
//			sendMessage(Text.create("Hello! ", Math.random()))
//		}

		val input = SparseEngine.window.input
		if (input[Key.U].pressed) {
			val item = Item(ItemTypeIronIngot)
//			item.color = random.nextInt(0xFFFFFF)
//			item.damage = random.nextInt(item.type.maxDurability)

			TestInventory.addItem(item)
			sendMessage(Text.create("Added item ${item.type}"))
		}
//		if (input[Key.J].pressed) {
//			sendMessage(Text.create("Removing item $item"))
//			TestInventory.removeItem(ItemStack(item, 3))
//		}

//		if (input[Key.K].pressed) {
//			item.color = TextColor.values().run { get(random.nextInt(size)).color }
//		}

		if (input[Key.KP_8].pressed) {
			TestInventory.clear()
		}

		if(input[Key.Y].pressed) {

			val entity = Minecraft.world.spawnEntity(EntityTypeItem, MinecraftClient.proxy.camera.transform.translation.clone())
			entity.velocity = MinecraftClient.proxy.camera.transform.rotation.forward * 10f
			entity.editData<EntityTypeItem.Data> {
				stack = ItemStack(ItemTypeApple)
			}

//			val selectedItem = TestInventory[selectedSlot]
//			if(selectedItem != null) {
//				TestInventory[selectedSlot] = null
//				val entity = Minecraft.world.spawnEntity(EntityTypeItem, MinecraftClient.proxy.camera.transform.translation.clone())
//				entity.editData<EntityTypeItem.Data> {
//					stack = selectedItem
//				}
//			}
		}

		if(input[Key.HOME].pressed) {
			MinecraftClient.proxy.camera.transform.translate(Vector3f(0f, 1f, 0f))
		}
	}

	override fun render(delta: Float) {

		//TODO: None of this is nearly final! All of these will probably be split into individual components

		val hotbarLeft = (manager.right / 2f) - (182f / 2f)
		val hotbarRight = (manager.right / 2f) + (182f / 2f)
		drawTexturedRectangle(
				"widgets/hotbar",
				hotbarLeft,
				manager.bottom,
				182f, 22f
		)

		drawTexturedRectangle("widgets/hotbar_selected", hotbarLeft - 1f + (selectedSlot * 20f), -1f, 24f)

//		drawTexturedRectangle((ItemType.diamondSword.proxy as ClientItemTypeProxy).sprite, hotbarLeft + 3f + 20f * 2, 3f, 16f, 16f)
//		drawItem(Item(ItemType.apple), 32, hotbarLeft + 3f + 20f * 2, 3f)
		for (i in 0 until 9) {
			val stack = TestInventory[i] ?: continue
			drawStack(stack, hotbarLeft + 3f + 20f * i, 3f)
		}


//		val exp = Math.sin(System.currentTimeMillis() / 500.0).toFloat() * 0.5f + 0.5f
		val exp = 0.452f
		drawTexturedRectangle("icons/exp_empty", hotbarLeft, 24f, 182f, 5f)
		drawTexturedRectangle("icons/exp_full", hotbarLeft, 24f, 182f * exp, 5f, exp)

		val level = 31
		val levelString = level.toString()
		val expTextWidth = TextRenderer.stringWidth(levelString)
		val expTextLeft = (manager.right / 2) - (expTextWidth / 2)
		val expTextBottom = 27f
		drawString(levelString, expTextLeft - 1f, expTextBottom, 0x000000, shadow = false)
		drawString(levelString, expTextLeft, expTextBottom - 1, 0x000000, shadow = false)
		drawString(levelString, expTextLeft + 1, expTextBottom, 0x000000, shadow = false)
		drawString(levelString, expTextLeft, expTextBottom + 1, 0x000000, shadow = false)
		drawString(levelString, expTextLeft, expTextBottom, 0x80FF20, shadow = false)


		for (i in 0 until 10) {
			drawTexturedRectangle("icons/heart_black_outline", hotbarLeft + (i * 8), 30f, 9f)
			if (i < 5) {
				drawTexturedRectangle("icons/heart_full", hotbarLeft + (i * 8), 30f, 9f)
			} else if (i == 5) {
				drawTexturedRectangle("icons/heart_half", hotbarLeft + (i * 8), 30f, 9f)
			}
		}

		for (i in 0 until 10) {
			drawTexturedRectangle("icons/food_black_outline", hotbarRight - ((i + 1) * 8) - 1, 30f, 9f)
			drawTexturedRectangle("icons/food_full", hotbarRight - ((i + 1) * 8) - 1, 30f, 9f)
		}

		glCall { glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA) }
		drawTexturedRectangle("icons/crosshair", manager.right / 2f - 16f / 2f, manager.top / 2f - 16f / 2f, 16f)
		glCall { glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) }

		drawTexturedRectangle("title/sparse_edition", 1f, 1f, 128f, 16f, color = 0xFFFFFF44)

		/*

			<---------- TEXT START --------->

		 */

		drawString(String.format("FPS: %.1f", SparseEngine.frameRate), 1f, manager.top - 9)
		drawString(String.format("MEM: %s", MemoryUsage.getMemoryUsedString()), 1f, manager.top - 18)

		val (camX, camY, camZ) = SparseEngine.game.camera.transform.translation
		drawString(String.format("POS: %.1f, %.1f, %.1f", camX, camY, camZ), 1f, manager.top - 27)


		for ((index, message) in chatMessages.withIndex()) {
			if (index >= 20) break
			val y = index * 9f + 39.5f

			drawRectangle(0f, y, 326f, 9f, (clamp(message.timeRemaining, 0f, 2f) * 64).toLong())
			drawText(message.text, 2f, y)
		}

	}
}

