package blue.sparse.minecraft.client.gui

import blue.sparse.engine.SparseEngine
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.util.MemoryUsage
import blue.sparse.engine.window.Window
import blue.sparse.engine.window.input.Key
import blue.sparse.math.clamp
import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.math.wrap
import blue.sparse.minecraft.client.MinecraftClient
import blue.sparse.minecraft.client.player.ClientPlayer
import blue.sparse.minecraft.client.text.TextRenderer
import blue.sparse.minecraft.client.world.proxy.ClientWorldProxy
import blue.sparse.minecraft.common.Minecraft
import blue.sparse.minecraft.common.block.BlockType
import blue.sparse.minecraft.common.entity.attribute.types.AttributeHealth
import blue.sparse.minecraft.common.entity.impl.types.EntityTypeItem
import blue.sparse.minecraft.common.inventory.impl.Section
import blue.sparse.minecraft.common.item.Item
import blue.sparse.minecraft.common.item.ItemType
import blue.sparse.minecraft.common.text.Text
import blue.sparse.minecraft.common.util.Identifier
import blue.sparse.minecraft.common.util.random
import org.lwjgl.opengl.GL11.*

object TestGUI : GUI() {
	var selectedSlot: Int = 0
		set(value) {
			field = wrap(value, 0, 8)
		}

	private val chatMessages = ArrayList<ChatMessage>()
	private var textBuffer: StringBuffer? = null

	override val overridingInput: Boolean
		get() = textBuffer != null

//	private val item = Item(ItemType.chainmailChestplate)

	fun sendMessage(message: Text) {
		chatMessages.add(0, ChatMessage(message, 10f))
	}

	fun sendMessage(message: String) {
		sendMessage(Text.create(message))
	}

	override fun update(delta: Float) {

		selectedSlot -= SparseEngine.window.input.scrollDelta.toInt()
//		selectedSlot = wrap(selectedSlot, 0, 8)

		chatMessages.removeAll {
			it.timeRemaining -= delta
			it.timeRemaining <= 0f
		}

//		if (Math.random() <= 0.01) {
//			sendMessage(Text.create("Hello! ", Math.random()))
//		}

		val input = SparseEngine.window.input

		val textBuffer = textBuffer
		if(textBuffer != null) {
			textBuffer.append(input.textBuffer)

			if(input[Key.BACKSPACE].pressed && textBuffer.isNotEmpty()) {
				textBuffer.deleteCharAt(textBuffer.length-1)
			}
			if(input[Key.ENTER].pressed) {
				val string = textBuffer.toString().trim()
				if(string.isNotBlank()) {
					if(string.startsWith("/tp")) {
						val split = string.split(" ")

						val x = split.getOrNull(1)?.toFloatOrNull() ?: 0f
						val y = split.getOrNull(2)?.toFloatOrNull() ?: 0f
						val z = split.getOrNull(3)?.toFloatOrNull() ?: 0f

						ClientPlayer.teleport(Vector3f(x, y, z), Minecraft.world)
						sendMessage("Teleported to $x $y $z")
					}else if(string.startsWith("/give")) {
						val split = string.split(" ")

						val itemName = split.getOrNull(1) ?: "debug"
						val item = ItemType.registry[Identifier(itemName)] ?: BlockType.debug.item!!

						val amount = split.getOrNull(2)?.toIntOrNull() ?: 1

						ClientPlayer.inventory += Item(item).stack(amount)

					}else {
						sendMessage(string)
					}
				}
				this.textBuffer = null
			}
			if(input[Key.ESCAPE].pressed) {
				this.textBuffer = null
			}

			if(this.textBuffer == null) {
//				input.window.cursorMode = Window.CursorMode.DISABLED
			}
			return
		}
		if(input[Key.T].pressed) {
			input.window.cursorMode = Window.CursorMode.NORMAL
			this.textBuffer = StringBuffer()
			return
		}

		if (input[Key.V].pressed || input[Key.V].heldTime >= 1f) {
//			val item = Item(ItemTypeIronIngot)

			val item = Item(arrayOf(BlockType.ironBlock, BlockType.goldBlock, BlockType.emeraldBlock, BlockType.diamondBlock, BlockType.coalBlock).random().item!!)

//			TestInventory.addItem(item)
			ClientPlayer.inventory.addItem(item)
			sendMessage(Text.create("Added item ${item.type}"))
		}

		for(i in 0 until 9) {
			val key = Key[Key.NUM_1.id + i]
			if(input[key].pressed)
				selectedSlot = i
		}

		if (input[Key.KP_8].pressed) {
			ClientPlayer.inventory.clear()
		}
		if(input[Key.KP_5].pressed) {
			Minecraft.world.entities.toList().forEach { it.remove() }
			Minecraft.players.forEach { it.entity?.add() }
		}

//		if(input[Key.Y].pressed || input[Key.Y].heldTime >= 2f) {
//			val entity = Minecraft.world.addEntity(EntityTypeItem, MinecraftClient.proxy.camera.transform.translation.clone())
//			entity.velocity = MinecraftClient.proxy.camera.transform.rotation.forward * 10f
//			entity.editData<EntityTypeItem.Data> {
//				stack = ItemStack(ItemTypeApple)
//			}
//		}
		if(input[Key.Q].pressed || input[Key.Q].heldTime >= 1f) {
			val selectedItem = ClientPlayer.inventory[Section.Key.Hotbar][selectedSlot]
			if(selectedItem != null) {
				selectedItem.amount--
				if(selectedItem.amount <= 0)
					ClientPlayer.inventory[Section.Key.Hotbar][selectedSlot] = null

				val entity = Minecraft.world.addEntity(EntityTypeItem, MinecraftClient.proxy.camera.transform.translation.clone())
				entity.velocity = MinecraftClient.proxy.camera.transform.rotation.forward * 10f
				entity.editData<EntityTypeItem.Data> {
					stack = selectedItem.deepCopy(1)
				}
			}
		}

		val player = ClientPlayer.entity
		if(player != null) {
			if(input[Key.K].pressed) {
				player[AttributeHealth] += 1f
				println(player[AttributeHealth])
			}
			if(input[Key.M].pressed) {
				player[AttributeHealth] -= 1f
				println(player[AttributeHealth])
			}
		}



//		if(input[Key.HOME].pressed) {
//			MinecraftClient.proxy.camera.transform.translate(Vector3f(0f, 1f, 0f))
//		}
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

		for (i in 0 until 9) {
			val stack = ClientPlayer.inventory[Section.Key.Hotbar][i] ?: continue
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

		val health = ClientPlayer.entity?.get(AttributeHealth) ?: 0.0f

		for (i in 0 until 10) {
			drawTexturedRectangle("icons/heart_black_outline", hotbarLeft + (i * 8), 30f, 9f)

			if(health >= (i+1) * 2) {
				drawTexturedRectangle("icons/heart_full", hotbarLeft + (i * 8), 30f, 9f)
			}else {
				val fractionalHeart = clamp((health - ((i) * 2)) / 2f, 0f, 1f)
				drawTexturedRectangle(
						"icons/heart_full",
						hotbarLeft + (i * 8),
						30f, 9f * fractionalHeart,
						9f,
						fractionalHeart
				)
			}

			// Traditional Minecraft half-hearts
//			else if(health >= (i+1) * 2 - 1) {
//				drawTexturedRectangle("icons/heart_half", hotbarLeft + (i * 8), 30f, 9f)
//			}
		}

		for (i in 0 until 10) {
			drawTexturedRectangle("icons/food_black_outline", hotbarRight - ((i + 1) * 8) - 1, 30f, 9f)
			drawTexturedRectangle("icons/food_full", hotbarRight - ((i + 1) * 8) - 1, 30f, 9f)
		}

		glCall { glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA) }
		drawTexturedRectangle("icons/crosshair", manager.right / 2f - 16f / 2f, manager.top / 2f - 16f / 2f, 16f)
		glCall { glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) }

		drawTexturedRectangle("title/sparse_edition", manager.right - 120f, 1f, 128f, 16f, color = 0xFFFFFF44)

		/*

			<---------- TEXT START --------->

		 */

		var line = 1
		fun line() = manager.top - 9 * (line++)

		drawString(String.format("FPS: %.1f", SparseEngine.frameRate), 1f, line())
		drawString(String.format("TPS: %.1f", Minecraft.tickRate), 1f, line())
		drawString(String.format("MEM: %s", MemoryUsage.getMemoryUsedString()), 1f, line())

		val (camX, camY, camZ) = SparseEngine.game.camera.transform.translation
		drawString(String.format("POS: %.1f, %.1f, %.1f", camX, camY, camZ), 1f, line())

		drawString("ENT: ${Minecraft.world.entities.size}", 1f, line())
		drawString("VIS: ${(Minecraft.world.proxy as ClientWorldProxy).renderer.visible}", 1f, line())

		val entity = ClientPlayer.entity
		if(entity != null) {
			val block = entity.block
//			drawString("TYP: ${block.type?.identifier}", 1f, line())
			drawString("BIO: ${block.biome.identifier}", 1f, line())
		}

		drawString("ITM: ${ClientPlayer.inventory[Section.Key.Hotbar][selectedSlot]?.item?.type?.identifier}", 1f, line())

		val textBuffer = textBuffer
		if(textBuffer != null) {
			val string = textBuffer.toString()
			val width = TextRenderer.stringWidth(string)
			drawRectangle(1f, 1f, manager.right-2f, 12f, 0x0000007F)
			drawString(string, 3f, 3f)
			drawRectangle(3f + width, 3f, 5f, 1f)
			drawRectangle(3f + width + 1f, 3f - 1f, 5f, 1f, 0x3F3F3FFF)
		}

		for ((index, message) in chatMessages.withIndex()) {
			if (index >= 20) break
			val y = index * 9f + 39.5f

			drawRectangle(0f, y, 326f, 9f, (clamp(message.timeRemaining, 0f, 2f) * 64).toLong())
			drawText(message.text, 2f, y)
		}

	}

	data class ChatMessage(val text: Text, var timeRemaining: Float)
}

