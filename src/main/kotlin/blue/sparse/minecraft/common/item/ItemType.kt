package blue.sparse.minecraft.common.item

import blue.sparse.minecraft.common.item.impl.*
import blue.sparse.minecraft.common.util.*

abstract class ItemType(val identifier: Identifier) {
	constructor(id: String): this(Identifier(id))

	init {
		register(this)
	}

	open val proxy: ItemTypeProxy by ProxyProvider<ItemTypeProxy>(
			"blue.sparse.minecraft.client.item.proxy.Default",
			"blue.sparse.minecraft.server.item.proxy.Default",
			this
	)

	abstract class ItemTypeProxy(val itemType: ItemType): Proxy

	companion object {

		internal val registry = LinkedHashMap<Identifier, ItemType>()

		private fun register(type: ItemType) {
			if(type.identifier in registry)
				throw IllegalArgumentException("Item with identifier \"${type.identifier}\" is already registered.")

			registry[type.identifier] = type
		}

//		val diamond = ItemDiamond
//		val emerald = ItemEmerald
//		val ironIngot = ItemIronIngot
//		val goldIngot = ItemGoldIngot
//		val coal = ItemCoal
//		val ruby = ItemRuby

		val acaciaBoat = ItemAcaciaBoat
		val apple = ItemApple
		val appleGolden = ItemAppleGolden
		val arrow = ItemArrow
		val barrier = ItemBarrier
		val beefCooked = ItemBeefCooked
		val beefRaw = ItemBeefRaw
		val beetroot = ItemBeetroot
		val beetrootSeeds = ItemBeetrootSeeds
		val beetrootSoup = ItemBeetrootSoup
		val birchBoat = ItemBirchBoat
		val blazePowder = ItemBlazePowder
		val blazeRod = ItemBlazeRod
		val bone = ItemBone
		val bookEnchanted = ItemBookEnchanted
		val bookNormal = ItemBookNormal
		val bookWritable = ItemBookWritable
		val bookWritten = ItemBookWritten
		val bowl = ItemBowl
		val bowStandby = ItemBowStandby
		val bread = ItemBread
		val brewingStand = ItemBrewingStand
		val brick = ItemBrick
		val brokenElytra = ItemBrokenElytra
		val bucketEmpty = ItemBucketEmpty
		val bucketLava = ItemBucketLava
		val bucketMilk = ItemBucketMilk
		val bucketWater = ItemBucketWater
		val cake = ItemCake
		val carrot = ItemCarrot
		val carrotGolden = ItemCarrotGolden
		val carrotOnAStick = ItemCarrotOnAStick
		val cauldron = ItemCauldron
		val chainmailBoots = ItemChainmailBoots
		val chainmailChestplate = ItemChainmailChestplate
		val chainmailHelmet = ItemChainmailHelmet
		val chainmailLeggings = ItemChainmailLeggings
		val charcoal = ItemCharcoal
		val chickenCooked = ItemChickenCooked
		val chickenRaw = ItemChickenRaw
		val chorusFruit = ItemChorusFruit
		val chorusFruitPopped = ItemChorusFruitPopped
		val clayBall = ItemClayBall
		val coal = ItemCoal
		val comparator = ItemComparator
		val cookie = ItemCookie
		val darkOakBoat = ItemDarkOakBoat
		val diamond = ItemDiamond
		val diamondAxe = ItemDiamondAxe
		val diamondBoots = ItemDiamondBoots
		val diamondChestplate = ItemDiamondChestplate
		val diamondHelmet = ItemDiamondHelmet
		val diamondHoe = ItemDiamondHoe
		val diamondHorseArmor = ItemDiamondHorseArmor
		val diamondLeggings = ItemDiamondLeggings
		val diamondPickaxe = ItemDiamondPickaxe
		val diamondShovel = ItemDiamondShovel
		val diamondSword = ItemDiamondSword
		val doorAcacia = ItemDoorAcacia
		val doorBirch = ItemDoorBirch
		val doorDarkOak = ItemDoorDarkOak
		val doorIron = ItemDoorIron
		val doorJungle = ItemDoorJungle
		val doorSpruce = ItemDoorSpruce
		val doorWood = ItemDoorWood
		val dragonBreath = ItemDragonBreath
		val dyePowderBlack = ItemDyePowderBlack
		val dyePowderBlue = ItemDyePowderBlue
		val dyePowderBrown = ItemDyePowderBrown
		val dyePowderCyan = ItemDyePowderCyan
		val dyePowderGray = ItemDyePowderGray
		val dyePowderGreen = ItemDyePowderGreen
		val dyePowderLightBlue = ItemDyePowderLightBlue
		val dyePowderLime = ItemDyePowderLime
		val dyePowderMagenta = ItemDyePowderMagenta
		val dyePowderOrange = ItemDyePowderOrange
		val dyePowderPink = ItemDyePowderPink
		val dyePowderPurple = ItemDyePowderPurple
		val dyePowderRed = ItemDyePowderRed
		val dyePowderSilver = ItemDyePowderSilver
		val dyePowderWhite = ItemDyePowderWhite
		val dyePowderYellow = ItemDyePowderYellow
		val egg = ItemEgg
		val elytra = ItemElytra
		val emerald = ItemEmerald
		val enderEye = ItemEnderEye
		val enderPearl = ItemEnderPearl
		val endCrystal = ItemEndCrystal
		val experienceBottle = ItemExperienceBottle
		val feather = ItemFeather
		val fireball = ItemFireball
		val fireworks = ItemFireworks
		val fishClownfishRaw = ItemFishClownfishRaw
		val fishCodCooked = ItemFishCodCooked
		val fishCodRaw = ItemFishCodRaw
		val fishPufferfishRaw = ItemFishPufferfishRaw
		val fishSalmonCooked = ItemFishSalmonCooked
		val fishSalmonRaw = ItemFishSalmonRaw
		val flint = ItemFlint
		val flintAndSteel = ItemFlintAndSteel
		val flowerPot = ItemFlowerPot
		val ghastTear = ItemGhastTear
		val glowstoneDust = ItemGlowstoneDust
		val goldAxe = ItemGoldAxe
		val goldBoots = ItemGoldBoots
		val goldChestplate = ItemGoldChestplate
		val goldHelmet = ItemGoldHelmet
		val goldHoe = ItemGoldHoe
		val goldHorseArmor = ItemGoldHorseArmor
		val goldIngot = ItemGoldIngot
		val goldLeggings = ItemGoldLeggings
		val goldNugget = ItemGoldNugget
		val goldPickaxe = ItemGoldPickaxe
		val goldShovel = ItemGoldShovel
		val goldSword = ItemGoldSword
		val gunpowder = ItemGunpowder
		val hopper = ItemHopper
		val ironAxe = ItemIronAxe
		val ironBoots = ItemIronBoots
		val ironChestplate = ItemIronChestplate
		val ironHelmet = ItemIronHelmet
		val ironHoe = ItemIronHoe
		val ironHorseArmor = ItemIronHorseArmor
		val ironIngot = ItemIronIngot
		val ironLeggings = ItemIronLeggings
		val ironNugget = ItemIronNugget
		val ironPickaxe = ItemIronPickaxe
		val ironShovel = ItemIronShovel
		val ironSword = ItemIronSword
		val itemFrame = ItemItemFrame
		val jungleBoat = ItemJungleBoat
		val knowledgeBook = ItemKnowledgeBook
		val lead = ItemLead
		val leather = ItemLeather
		val magmaCream = ItemMagmaCream
		val mapEmpty = ItemMapEmpty
//		val mapFilled = ItemMapFilled
//		val mapFilledMarkings = ItemMapFilledMarkings
		val melon = ItemMelon
		val melonSpeckled = ItemMelonSpeckled
		val minecartChest = ItemMinecartChest
		val minecartCommandBlock = ItemMinecartCommandBlock
		val minecartFurnace = ItemMinecartFurnace
		val minecartHopper = ItemMinecartHopper
		val minecartNormal = ItemMinecartNormal
		val minecartTnt = ItemMinecartTnt
		val mushroomStew = ItemMushroomStew
		val muttonCooked = ItemMuttonCooked
		val muttonRaw = ItemMuttonRaw
		val nameTag = ItemNameTag
		val netherbrick = ItemNetherBrick
		val netherStar = ItemNetherStar
		val netherWart = ItemNetherWart
		val oakBoat = ItemOakBoat
		val painting = ItemPainting
		val paper = ItemPaper
		val porkchopCooked = ItemPorkChopCooked
		val porkchopRaw = ItemPorkChopRaw
		val potato = ItemPotato
		val potatoBaked = ItemPotatoBaked
		val potatoPoisonous = ItemPotatoPoisonous
		val prismarineCrystals = ItemPrismarineCrystals
		val prismarineShard = ItemPrismarineShard
		val pumpkinPie = ItemPumpkinPie
		val quartz = ItemQuartz
		val rabbitCooked = ItemRabbitCooked
		val rabbitFoot = ItemRabbitFoot
		val rabbitHide = ItemRabbitHide
		val rabbitRaw = ItemRabbitRaw
		val rabbitStew = ItemRabbitStew
		val recordBlocks = ItemRecordBlocks
		val recordCat = ItemRecordCat
		val recordChirp = ItemRecordChirp
		val recordFar = ItemRecordFar
		val recordMall = ItemRecordMall
		val recordMellohi = ItemRecordMellohi
		val recordStal = ItemRecordStal
		val recordStrad = ItemRecordStrad
		val recordWait = ItemRecordWait
		val recordWard = ItemRecordWard
		val redstoneDust = ItemRedstoneDust
		val reeds = ItemReeds
		val repeater = ItemRepeater
		val rottenFlesh = ItemRottenFlesh
		val ruby = ItemRuby
		val saddle = ItemSaddle
		val seedsMelon = ItemSeedsMelon
		val seedsPumpkin = ItemSeedsPumpkin
		val seedsWheat = ItemSeedsWheat
		val shears = ItemShears
		val shulkerShell = ItemShulkerShell
		val sign = ItemSign
		val slimeball = ItemSlimeBall
		val snowball = ItemSnowball
		val spectralArrow = ItemSpectralArrow
		val spiderEye = ItemSpiderEye
		val spiderEyeFermented = ItemSpiderEyeFermented
		val spruceBoat = ItemSpruceBoat
		val stick = ItemStick
		val stoneAxe = ItemStoneAxe
		val stoneHoe = ItemStoneHoe
		val stonePickaxe = ItemStonePickaxe
		val stoneShovel = ItemStoneShovel
		val stoneSword = ItemStoneSword
		val string = ItemString
		val structureVoid = ItemStructureVoid
		val sugar = ItemSugar
		val totem = ItemTotem
		val wheat = ItemWheat
		val woodenArmorstand = ItemWoodenArmorStand
		val woodAxe = ItemWoodAxe
		val woodHoe = ItemWoodHoe
		val woodPickaxe = ItemWoodPickaxe
		val woodShovel = ItemWoodShovel
		val woodSword = ItemWoodSword
	}
}