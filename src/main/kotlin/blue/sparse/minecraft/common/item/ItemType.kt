package blue.sparse.minecraft.common.item

import blue.sparse.minecraft.common.item.impl.*
import blue.sparse.minecraft.common.util.*

abstract class ItemType(val identifier: Identifier, val maxStackSize: Int = 64) {
	constructor(id: String, maxStackSize: Int = 64): this(Identifier(id), maxStackSize)

	init {
		register(this)
	}

	open val proxy: ItemTypeProxy by ProxyProvider<ItemTypeProxy>(
			"blue.sparse.minecraft.client.item.proxy.Default",
			"blue.sparse.minecraft.server.item.proxy.Default",
			this
	)

	override fun toString() = identifier.toString()

	abstract class ItemTypeProxy(val itemType: ItemType): Proxy

	companion object {

		internal val registry = LinkedHashMap<Identifier, ItemType>()

		private fun register(type: ItemType) {
			if(type.identifier in registry)
				throw IllegalArgumentException("Item with identifier \"${type.identifier}\" is already registered.")

			registry[type.identifier] = type
		}

//		val diamond = ItemTypeDiamond
//		val emerald = ItemTypeEmerald
//		val ironIngot = ItemTypeIronIngot
//		val goldIngot = ItemTypeGoldIngot
//		val coal = ItemTypeCoal
//		val ruby = ItemTypeRuby

		val acaciaBoat = ItemTypeAcaciaBoat
		val apple = ItemTypeApple
		val appleGolden = ItemTypeAppleGolden
		val arrow = ItemTypeArrow
		val barrier = ItemTypeBarrier
		val beefCooked = ItemTypeBeefCooked
		val beefRaw = ItemTypeBeefRaw
		val beetroot = ItemTypeBeetroot
		val beetrootSeeds = ItemTypeBeetrootSeeds
		val beetrootSoup = ItemTypeBeetrootSoup
		val birchBoat = ItemTypeBirchBoat
		val blazePowder = ItemTypeBlazePowder
		val blazeRod = ItemTypeBlazeRod
		val bone = ItemTypeBone
		val bookEnchanted = ItemTypeBookEnchanted
		val bookNormal = ItemTypeBookNormal
		val bookWritable = ItemTypeBookWritable
		val bookWritten = ItemTypeBookWritten
		val bowl = ItemTypeBowl
		val bread = ItemTypeBread
		val brewingStand = ItemTypeBrewingStand
		val brick = ItemTypeBrick
		val brokenElytra = ItemTypeBrokenElytra
		val bucketEmpty = ItemTypeBucketEmpty
		val bucketLava = ItemTypeBucketLava
		val bucketMilk = ItemTypeBucketMilk
		val bucketWater = ItemTypeBucketWater
		val cake = ItemTypeCake
		val carrot = ItemTypeCarrot
		val carrotGolden = ItemTypeCarrotGolden
		val carrotOnAStick = ItemTypeCarrotOnAStick
		val cauldron = ItemTypeCauldron
		val chainmailBoots = ItemTypeChainmailBoots
		val chainmailChestplate = ItemTypeChainmailChestplate
		val chainmailHelmet = ItemTypeChainmailHelmet
		val chainmailLeggings = ItemTypeChainmailLeggings
		val charcoal = ItemTypeCharcoal
		val chickenCooked = ItemTypeChickenCooked
		val chickenRaw = ItemTypeChickenRaw
		val chorusFruit = ItemTypeChorusFruit
		val chorusFruitPopped = ItemTypeChorusFruitPopped
		val clayBall = ItemTypeClayBall
		val coal = ItemTypeCoal
		val comparator = ItemTypeComparator
		val cookie = ItemTypeCookie
		val darkOakBoat = ItemTypeDarkOakBoat
		val diamond = ItemTypeDiamond
		val diamondAxe = ItemTypeDiamondAxe
		val diamondBoots = ItemTypeDiamondBoots
		val diamondChestplate = ItemTypeDiamondChestplate
		val diamondHelmet = ItemTypeDiamondHelmet
		val diamondHoe = ItemTypeDiamondHoe
		val diamondHorseArmor = ItemTypeDiamondHorseArmor
		val diamondLeggings = ItemTypeDiamondLeggings
		val diamondPickaxe = ItemTypeDiamondPickaxe
		val diamondShovel = ItemTypeDiamondShovel
		val diamondSword = ItemTypeDiamondSword
		val doorAcacia = ItemTypeDoorAcacia
		val doorBirch = ItemTypeDoorBirch
		val doorDarkOak = ItemTypeDoorDarkOak
		val doorIron = ItemTypeDoorIron
		val doorJungle = ItemTypeDoorJungle
		val doorSpruce = ItemTypeDoorSpruce
		val doorWood = ItemTypeDoorWood
		val dragonBreath = ItemTypeDragonBreath
		val dyePowderBlack = ItemTypeDyePowderBlack
		val dyePowderBlue = ItemTypeDyePowderBlue
		val dyePowderBrown = ItemTypeDyePowderBrown
		val dyePowderCyan = ItemTypeDyePowderCyan
		val dyePowderGray = ItemTypeDyePowderGray
		val dyePowderGreen = ItemTypeDyePowderGreen
		val dyePowderLightBlue = ItemTypeDyePowderLightBlue
		val dyePowderLime = ItemTypeDyePowderLime
		val dyePowderMagenta = ItemTypeDyePowderMagenta
		val dyePowderOrange = ItemTypeDyePowderOrange
		val dyePowderPink = ItemTypeDyePowderPink
		val dyePowderPurple = ItemTypeDyePowderPurple
		val dyePowderRed = ItemTypeDyePowderRed
		val dyePowderSilver = ItemTypeDyePowderSilver
		val dyePowderWhite = ItemTypeDyePowderWhite
		val dyePowderYellow = ItemTypeDyePowderYellow
		val egg = ItemTypeEgg
		val elytra = ItemTypeElytra
		val emerald = ItemTypeEmerald
		val enderEye = ItemTypeEnderEye
		val enderPearl = ItemTypeEnderPearl
		val endCrystal = ItemTypeEndCrystal
		val experienceBottle = ItemTypeExperienceBottle
		val feather = ItemTypeFeather
		val fireball = ItemTypeFireball
		val fireworks = ItemTypeFireworks
		val fishClownfishRaw = ItemTypeFishClownfishRaw
		val fishCodCooked = ItemTypeFishCodCooked
		val fishCodRaw = ItemTypeFishCodRaw
		val fishPufferfishRaw = ItemTypeFishPufferfishRaw
		val fishSalmonCooked = ItemTypeFishSalmonCooked
		val fishSalmonRaw = ItemTypeFishSalmonRaw
		val flint = ItemTypeFlint
		val flintAndSteel = ItemTypeFlintAndSteel
		val flowerPot = ItemTypeFlowerPot
		val ghastTear = ItemTypeGhastTear
		val glowstoneDust = ItemTypeGlowstoneDust
		val goldAxe = ItemTypeGoldAxe
		val goldBoots = ItemTypeGoldBoots
		val goldChestplate = ItemTypeGoldChestplate
		val goldHelmet = ItemTypeGoldHelmet
		val goldHoe = ItemTypeGoldHoe
		val goldHorseArmor = ItemTypeGoldHorseArmor
		val goldIngot = ItemTypeGoldIngot
		val goldLeggings = ItemTypeGoldLeggings
		val goldNugget = ItemTypeGoldNugget
		val goldPickaxe = ItemTypeGoldPickaxe
		val goldShovel = ItemTypeGoldShovel
		val goldSword = ItemTypeGoldSword
		val gunpowder = ItemTypeGunpowder
		val hopper = ItemTypeHopper
		val ironAxe = ItemTypeIronAxe
		val ironBoots = ItemTypeIronBoots
		val ironChestplate = ItemTypeIronChestplate
		val ironHelmet = ItemTypeIronHelmet
		val ironHoe = ItemTypeIronHoe
		val ironHorseArmor = ItemTypeIronHorseArmor
		val ironIngot = ItemTypeIronIngot
		val ironLeggings = ItemTypeIronLeggings
		val ironNugget = ItemTypeIronNugget
		val ironPickaxe = ItemTypeIronPickaxe
		val ironShovel = ItemTypeIronShovel
		val ironSword = ItemTypeIronSword
		val itemFrame = ItemTypeItemFrame
		val jungleBoat = ItemTypeJungleBoat
		val knowledgeBook = ItemTypeKnowledgeBook
		val lead = ItemTypeLead
		val leather = ItemTypeLeather
		val magmaCream = ItemTypeMagmaCream
		val mapEmpty = ItemTypeMapEmpty
//		val mapFilled = ItemTypeMapFilled
//		val mapFilledMarkings = ItemTypeMapFilledMarkings
		val melon = ItemTypeMelon
		val melonSpeckled = ItemTypeMelonSpeckled
		val minecartChest = ItemTypeMinecartChest
		val minecartCommandBlock = ItemTypeMinecartCommandBlock
		val minecartFurnace = ItemTypeMinecartFurnace
		val minecartHopper = ItemTypeMinecartHopper
		val minecartNormal = ItemTypeMinecartNormal
		val minecartTnt = ItemTypeMinecartTnt
		val mushroomStew = ItemTypeMushroomStew
		val muttonCooked = ItemTypeMuttonCooked
		val muttonRaw = ItemTypeMuttonRaw
		val nameTag = ItemTypeNameTag
		val netherbrick = ItemTypeNetherBrick
		val netherStar = ItemTypeNetherStar
		val netherWart = ItemTypeNetherWart
		val oakBoat = ItemTypeOakBoat
		val painting = ItemTypePainting
		val paper = ItemTypePaper
		val porkchopCooked = ItemTypePorkChopCooked
		val porkchopRaw = ItemTypePorkChopRaw
		val potato = ItemTypePotato
		val potatoBaked = ItemTypePotatoBaked
		val potatoPoisonous = ItemTypePotatoPoisonous
		val prismarineCrystals = ItemTypePrismarineCrystals
		val prismarineShard = ItemTypePrismarineShard
		val pumpkinPie = ItemTypePumpkinPie
		val quartz = ItemTypeQuartz
		val rabbitCooked = ItemTypeRabbitCooked
		val rabbitFoot = ItemTypeRabbitFoot
		val rabbitHide = ItemTypeRabbitHide
		val rabbitRaw = ItemTypeRabbitRaw
		val rabbitStew = ItemTypeRabbitStew
		val recordBlocks = ItemTypeRecordBlocks
		val recordCat = ItemTypeRecordCat
		val recordChirp = ItemTypeRecordChirp
		val recordFar = ItemTypeRecordFar
		val recordMall = ItemTypeRecordMall
		val recordMellohi = ItemTypeRecordMellohi
		val recordStal = ItemTypeRecordStal
		val recordStrad = ItemTypeRecordStrad
		val recordWait = ItemTypeRecordWait
		val recordWard = ItemTypeRecordWard
		val redstoneDust = ItemTypeRedstoneDust
		val reeds = ItemTypeReeds
		val repeater = ItemTypeRepeater
		val rottenFlesh = ItemTypeRottenFlesh
		val ruby = ItemTypeRuby
		val saddle = ItemTypeSaddle
		val seedsMelon = ItemTypeSeedsMelon
		val seedsPumpkin = ItemTypeSeedsPumpkin
		val seedsWheat = ItemTypeSeedsWheat
		val shears = ItemTypeShears
		val shulkerShell = ItemTypeShulkerShell
		val sign = ItemTypeSign
		val slimeball = ItemTypeSlimeBall
		val snowball = ItemTypeSnowball
		val spectralArrow = ItemTypeSpectralArrow
		val spiderEye = ItemTypeSpiderEye
		val spiderEyeFermented = ItemTypeSpiderEyeFermented
		val spruceBoat = ItemTypeSpruceBoat
		val stick = ItemTypeStick
		val stoneAxe = ItemTypeStoneAxe
		val stoneHoe = ItemTypeStoneHoe
		val stonePickaxe = ItemTypeStonePickaxe
		val stoneShovel = ItemTypeStoneShovel
		val stoneSword = ItemTypeStoneSword
		val string = ItemTypeString
		val structureVoid = ItemTypeStructureVoid
		val sugar = ItemTypeSugar
		val totem = ItemTypeTotem
		val wheat = ItemTypeWheat
		val woodAxe = ItemTypeWoodAxe
		val woodHoe = ItemTypeWoodHoe
		val woodPickaxe = ItemTypeWoodPickaxe
		val woodShovel = ItemTypeWoodShovel
		val woodSword = ItemTypeWoodSword
	}
}