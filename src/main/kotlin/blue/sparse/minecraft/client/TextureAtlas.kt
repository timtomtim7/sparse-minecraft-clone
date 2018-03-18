package blue.sparse.minecraft.client

import blue.sparse.engine.asset.Asset
import blue.sparse.engine.errors.glCall
import blue.sparse.engine.render.resource.Texture
import blue.sparse.engine.render.resource.bind
import blue.sparse.math.vectors.floats.Vector4f
import blue.sparse.math.vectors.ints.Vector2i
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import java.awt.image.BufferedImage

class TextureAtlas(size: Vector2i) {

	private val sprites = LinkedHashMap<String, Sprite>()

	val texture = Texture(BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB))

	val size = size
		get() = field.clone()

	init {
		texture.bind {
			glCall { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE) }
			glCall { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE) }
			glCall { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST) }
			glCall { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST) }
		}
	}

	operator fun get(name: String): Sprite? {
		return sprites[name]
	}

	fun getOrAddSprite(name: String): Sprite {
		sprites[name]?.let { return it }
		return addSprite(name, Asset[name])!!
	}

	fun getOrAddSprite(asset: Asset): Sprite {
		sprites[asset.path]?.let { return it }
		return addSprite(asset.path, asset)!!
	}

//	fun getOrAddSprite(name: String, asset: Asset): Sprite {
//		sprites[name]?.let { return it }
//
//		addSprite(name, asset.readImage())
//		return sprites[name]!!
//	}

	fun getOrAddSprite(name: String, image: BufferedImage): Sprite {
		sprites[name]?.let { return it }

		addSprite(name, image)
		return sprites[name]!!
	}

	fun addSprite(name: String, asset: Asset = Asset[name]): Sprite? {
		return addSprite(name, asset.readImage())
	}

	fun addSprite(name: String, image: BufferedImage): Sprite? {
		if(name in sprites)
			throw IllegalStateException("Sprite with the name \"$name\" already present.")

		val size = Vector2i(image.width, image.height)
		val position = findAvailableSpace(size) ?: return null

		texture.subImage(image, position)
		val sprite = Sprite(name, position, size)
		sprites[name] = sprite

		return sprite
	}

	private fun findAvailableSpace(spriteSize: Vector2i): Vector2i? {
		val min = Vector2i(0)
//		val min = Vector2i(
//				random.nextInt(size.x / spriteSize.x) * spriteSize.x,
//				random.nextInt(size.y / spriteSize.y) * spriteSize.y
//		)

		var free = fits(min, min+spriteSize)

		if(free)
			return min

		for(sprite in sprites.values) {
			min.x = sprite.max.x
			min.y = sprite.min.y
			free = fits(min, min+spriteSize)
			if(free) break

			min.x = sprite.min.x
			min.y = sprite.max.y
			free = fits(min, min+spriteSize)
			if(free) break

			min.x = sprite.max.x
			min.y = sprite.max.y
			free = fits(min, min+spriteSize)
			if(free) break
		}

		if(!free)
			return null

		return min
	}

	private fun fits(min: Vector2i, max: Vector2i): Boolean {
		if(min.x < 0 || min.y < 0 || max.x > size.x || max.y > size.y)
			return false

		if(findIntersectingSprite(min, max) != null)
			return false

		return true
	}

	private fun findIntersectingSprite(min: Vector2i, max: Vector2i): Sprite? {
		val leftA = min.x
		val rightA = max.x
		val topA = min.y
		val bottomA = max.y

		for(sprite in sprites.values) {
			val leftB = sprite.min.x
			val rightB = sprite.max.x
			val topB = sprite.min.y
			val bottomB = sprite.max.y

			if(leftA < rightB && rightA > leftB && topA < bottomB && bottomA > topB)
				return sprite
		}

		return null
	}

	inner class Sprite (val name: String, pos: Vector2i, size: Vector2i) {
		val min = pos.clone()
			get() = field.clone()

		val max = pos + size
			get() = field.clone()

//		val size = size.clone()
//			get() = field.clone()

		val textureCoords: Vector4f
			get() = Vector4f(
					min.toFloatVector() / size.toFloatVector(),
					max.toFloatVector() / size.toFloatVector()
			)

		val atlas = this@TextureAtlas
	}
}