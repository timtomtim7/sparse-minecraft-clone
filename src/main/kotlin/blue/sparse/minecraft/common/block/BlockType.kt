package blue.sparse.minecraft.common.block

import blue.sparse.math.vectors.floats.Vector3f
import blue.sparse.minecraft.common.item.types.ItemTypeBlock
import blue.sparse.minecraft.common.util.Identifier
import blue.sparse.minecraft.common.util.math.AABB
import blue.sparse.minecraft.common.util.proxy.Proxy
import blue.sparse.minecraft.common.util.proxy.ProxyProvider

abstract class BlockType(val identifier: Identifier, val hasItem: Boolean = true) {
	internal val id: Int

	open val proxy: BlockTypeProxy by ProxyProvider<BlockTypeProxy>(
			"blue.sparse.minecraft.client.block.proxy.Default",
			"blue.sparse.minecraft.server.block.proxy.Default",
			this
	)

	val item = if(hasItem) ItemTypeBlock(this) else null

	open val bounds = AABB(Vector3f(0f), Vector3f(1f))

	open val transparent: Boolean = false

	constructor(id: String) : this(Identifier(id))

	init {
		id = registry.size + 1
		register(this)
	}

	abstract class BlockTypeProxy(val blockType: BlockType) : Proxy

	companion object {
		internal val registry = LinkedHashMap<Identifier, BlockType>()
		private val idRegistry = LinkedHashMap<Int, BlockType>()

		private fun register(type: BlockType) {
			if (type.identifier in registry)
				throw IllegalArgumentException("Block with identifier \"${type.identifier}\" is already registered.")

			registry[type.identifier] = type
			idRegistry[type.id] = type
		}

		operator fun get(identifier: Identifier) = registry[identifier]

		operator fun get(name: String) = get(Identifier(name))

		internal operator fun get(id: Int) = idRegistry[id]

//		val stone = BlockStone
//		val dirt = BlockDirt
//		val cobblestone = BlockCobblestone
//		val sand = BlockSand

//		val anvilBase                           = BlockAnvilBase
//		val beacon                              = BlockBeacon
		val bedrock                             = BlockBedrock
//		val bookshelf                           = BlockBookshelf
//		val brewingStand                        = BlockBrewingStand
//		val brewingStandBase                    = BlockBrewingStandBase
//		val brick                               = BlockBrick
//		val cakeInner                           = BlockCakeInner
//		val cauldronInner                       = BlockCauldronInner
//		val chainCommandBlockBack               = BlockChainCommandBlockBack
//		val chainCommandBlockConditional        = BlockChainCommandBlockConditional
//		val chainCommandBlockFront              = BlockChainCommandBlockFront
//		val chorusFlower                        = BlockChorusFlower
//		val chorusFlowerDead                    = BlockChorusFlowerDead
//		val chorusPlant                         = BlockChorusPlant
		val clay                                = BlockClay
		val coalBlock                           = BlockCoalBlock
		val coalOre                             = BlockCoalOre
//		val coarseDirt                          = BlockCoarseDirt
		val cobblestone                         = BlockCobblestone
		val cobblestoneMossy                    = BlockCobblestoneMossy
//		val commandBlockBack                    = BlockCommandBlockBack
//		val commandBlockConditional             = BlockCommandBlockConditional
//		val commandBlockFront                   = BlockCommandBlockFront
//		val comparatorOff                       = BlockComparatorOff
//		val comparatorOn                        = BlockComparatorOn
		val concreteBlack                       = BlockConcreteBlack
		val concreteBlue                        = BlockConcreteBlue
		val concreteBrown                       = BlockConcreteBrown
		val concreteCyan                        = BlockConcreteCyan
		val concreteGray                        = BlockConcreteGray
		val concreteGreen                       = BlockConcreteGreen
		val concreteLightBlue                   = BlockConcreteLightBlue
		val concreteLime                        = BlockConcreteLime
		val concreteMagenta                     = BlockConcreteMagenta
		val concreteOrange                      = BlockConcreteOrange
		val concretePink                        = BlockConcretePink
		val concretePowderBlack                 = BlockConcretePowderBlack
		val concretePowderBlue                  = BlockConcretePowderBlue
		val concretePowderBrown                 = BlockConcretePowderBrown
		val concretePowderCyan                  = BlockConcretePowderCyan
		val concretePowderGray                  = BlockConcretePowderGray
		val concretePowderGreen                 = BlockConcretePowderGreen
		val concretePowderLightBlue             = BlockConcretePowderLightBlue
		val concretePowderLime                  = BlockConcretePowderLime
		val concretePowderMagenta               = BlockConcretePowderMagenta
		val concretePowderOrange                = BlockConcretePowderOrange
		val concretePowderPink                  = BlockConcretePowderPink
		val concretePowderPurple                = BlockConcretePowderPurple
		val concretePowderRed                   = BlockConcretePowderRed
		val concretePowderSilver                = BlockConcretePowderSilver
		val concretePowderWhite                 = BlockConcretePowderWhite
		val concretePowderYellow                = BlockConcretePowderYellow
		val concretePurple                      = BlockConcretePurple
		val concreteRed                         = BlockConcreteRed
		val concreteSilver                      = BlockConcreteSilver
		val concreteWhite                       = BlockConcreteWhite
		val concreteYellow                      = BlockConcreteYellow
//		val craftingTableFront                  = BlockCraftingTableFront
//		val deadbush                            = BlockDeadbush
		val debug                               = BlockDebug
		val diamondBlock                        = BlockDiamondBlock
		val diamondOre                          = BlockDiamondOre
		val dirt                                = BlockDirt
//		val dispenserFrontHorizontal            = BlockDispenserFrontHorizontal
//		val dispenserFrontVertical              = BlockDispenserFrontVertical
//		val doorAcaciaLower                     = BlockDoorAcaciaLower
//		val doorAcaciaUpper                     = BlockDoorAcaciaUpper
//		val doorBirchLower                      = BlockDoorBirchLower
//		val doorBirchUpper                      = BlockDoorBirchUpper
//		val doorDarkOakLower                    = BlockDoorDarkOakLower
//		val doorDarkOakUpper                    = BlockDoorDarkOakUpper
//		val doorIronLower                       = BlockDoorIronLower
//		val doorIronUpper                       = BlockDoorIronUpper
//		val doorJungleLower                     = BlockDoorJungleLower
//		val doorJungleUpper                     = BlockDoorJungleUpper
//		val doorSpruceLower                     = BlockDoorSpruceLower
//		val doorSpruceUpper                     = BlockDoorSpruceUpper
//		val doorWoodLower                       = BlockDoorWoodLower
//		val doorWoodUpper                       = BlockDoorWoodUpper
//		val doublePlantSunflowerBack            = BlockDoublePlantSunflowerBack
//		val doublePlantSunflowerFront           = BlockDoublePlantSunflowerFront
//		val dragonEgg                           = BlockDragonEgg
//		val dropperFrontHorizontal              = BlockDropperFrontHorizontal
//		val dropperFrontVertical                = BlockDropperFrontVertical
		val emeraldBlock                        = BlockEmeraldBlock
		val emeraldOre                          = BlockEmeraldOre
//		val endframeEye                         = BlockEndframeEye
		val endBricks                           = BlockEndBricks
//		val endRod                              = BlockEndRod
		val endStone                            = BlockEndStone
//		val farmlandDry                         = BlockFarmlandDry
//		val farmlandWet                         = BlockFarmlandWet
//		val fern                                = BlockFern
//		val flowerAllium                        = BlockFlowerAllium
//		val flowerBlueOrchid                    = BlockFlowerBlueOrchid
//		val flowerDandelion                     = BlockFlowerDandelion
//		val flowerHoustonia                     = BlockFlowerHoustonia
//		val flowerOxeyeDaisy                    = BlockFlowerOxeyeDaisy
//		val flowerPaeonia                       = BlockFlowerPaeonia
//		val flowerPot                           = BlockFlowerPot
//		val flowerRose                          = BlockFlowerRose
//		val flowerTulipOrange                   = BlockFlowerTulipOrange
//		val flowerTulipPink                     = BlockFlowerTulipPink
//		val flowerTulipRed                      = BlockFlowerTulipRed
//		val flowerTulipWhite                    = BlockFlowerTulipWhite
//		val furnaceFrontOff                     = BlockFurnaceFrontOff
//		val furnaceFrontOn                      = BlockFurnaceFrontOn
		val glass                               = BlockGlass
//		val glassBlack                          = BlockGlassBlack
//		val glassBlue                           = BlockGlassBlue
//		val glassBrown                          = BlockGlassBrown
//		val glassCyan                           = BlockGlassCyan
//		val glassGray                           = BlockGlassGray
//		val glassGreen                          = BlockGlassGreen
//		val glassLightBlue                      = BlockGlassLightBlue
//		val glassLime                           = BlockGlassLime
//		val glassMagenta                        = BlockGlassMagenta
//		val glassOrange                         = BlockGlassOrange
//		val glassPink                           = BlockGlassPink
//		val glassPurple                         = BlockGlassPurple
//		val glassRed                            = BlockGlassRed
//		val glassSilver                         = BlockGlassSilver
//		val glassWhite                          = BlockGlassWhite
//		val glassYellow                         = BlockGlassYellow
//		val glazedTerracottaBlack               = BlockGlazedTerracottaBlack
//		val glazedTerracottaBlue                = BlockGlazedTerracottaBlue
//		val glazedTerracottaBrown               = BlockGlazedTerracottaBrown
//		val glazedTerracottaCyan                = BlockGlazedTerracottaCyan
//		val glazedTerracottaGray                = BlockGlazedTerracottaGray
//		val glazedTerracottaGreen               = BlockGlazedTerracottaGreen
//		val glazedTerracottaLightBlue           = BlockGlazedTerracottaLightBlue
//		val glazedTerracottaLime                = BlockGlazedTerracottaLime
//		val glazedTerracottaMagenta             = BlockGlazedTerracottaMagenta
//		val glazedTerracottaOrange              = BlockGlazedTerracottaOrange
//		val glazedTerracottaPink                = BlockGlazedTerracottaPink
//		val glazedTerracottaPurple              = BlockGlazedTerracottaPurple
//		val glazedTerracottaRed                 = BlockGlazedTerracottaRed
//		val glazedTerracottaSilver              = BlockGlazedTerracottaSilver
//		val glazedTerracottaWhite               = BlockGlazedTerracottaWhite
//		val glazedTerracottaYellow              = BlockGlazedTerracottaYellow
		val glowstone                           = BlockGlowstone
		val goldBlock                           = BlockGoldBlock
		val goldOre                             = BlockGoldOre
		val grass								= BlockGrass
		val gravel                              = BlockGravel
		val hardenedClay                        = BlockHardenedClay
//		val hardenedClayStainedBlack            = BlockHardenedClayStainedBlack
//		val hardenedClayStainedBlue             = BlockHardenedClayStainedBlue
//		val hardenedClayStainedBrown            = BlockHardenedClayStainedBrown
//		val hardenedClayStainedCyan             = BlockHardenedClayStainedCyan
//		val hardenedClayStainedGray             = BlockHardenedClayStainedGray
//		val hardenedClayStainedGreen            = BlockHardenedClayStainedGreen
//		val hardenedClayStainedLightBlue        = BlockHardenedClayStainedLightBlue
//		val hardenedClayStainedLime             = BlockHardenedClayStainedLime
//		val hardenedClayStainedMagenta          = BlockHardenedClayStainedMagenta
//		val hardenedClayStainedOrange           = BlockHardenedClayStainedOrange
//		val hardenedClayStainedPink             = BlockHardenedClayStainedPink
//		val hardenedClayStainedPurple           = BlockHardenedClayStainedPurple
//		val hardenedClayStainedRed              = BlockHardenedClayStainedRed
//		val hardenedClayStainedSilver           = BlockHardenedClayStainedSilver
//		val hardenedClayStainedWhite            = BlockHardenedClayStainedWhite
//		val hardenedClayStainedYellow           = BlockHardenedClayStainedYellow
		val ice                                 = BlockIce
		val icePacked                           = BlockIcePacked
//		val ironBars                            = BlockIronBars
		val ironBlock                           = BlockIronBlock
		val ironOre                             = BlockIronOre
//		val ironTrapdoor                        = BlockIronTrapdoor
//		val itemframeBackground                 = BlockItemframeBackground
//		val ladder                              = BlockLadder
		val lapisBlock                          = BlockLapisBlock
		val lapisOre                            = BlockLapisOre
//		val lavaFlow                            = BlockLavaFlow
//		val lavaStill                           = BlockLavaStill
//		val leavesAcacia                        = BlockLeavesAcacia
//		val leavesBigOak                        = BlockLeavesBigOak
//		val leavesBirch                         = BlockLeavesBirch
//		val leavesJungle                        = BlockLeavesJungle
//		val leavesOak                           = BlockLeavesOak
//		val leavesSpruce                        = BlockLeavesSpruce
//		val lever                               = BlockLever
//		val logAcacia                           = BlockLogAcacia
//		val logBigOak                           = BlockLogBigOak
//		val logBirch                            = BlockLogBirch
//		val logJungle                           = BlockLogJungle
//		val logOak                              = BlockLogOak
//		val logSpruce                           = BlockLogSpruce
		val magma                               = BlockMagma
//		val melonStemConnected                  = BlockMelonStemConnected
//		val melonStemDisconnected               = BlockMelonStemDisconnected
		val mobSpawner                          = BlockMobSpawner
//		val mushroomBlockSkinBrown              = BlockMushroomBlockSkinBrown
//		val mushroomBlockSkinRed                = BlockMushroomBlockSkinRed
//		val mushroomBlockSkinStem               = BlockMushroomBlockSkinStem
//		val mushroomBrown                       = BlockMushroomBrown
//		val mushroomRed                         = BlockMushroomRed
		val netherrack                          = BlockNetherrack
		val netherBrick                         = BlockNetherBrick
		val netherWartBlock                     = BlockNetherWartBlock
		val noteblock                           = BlockNoteblock
//		val observerBack                        = BlockObserverBack
//		val observerBackLit                     = BlockObserverBackLit
//		val observerFront                       = BlockObserverFront
		val obsidian                            = BlockObsidian
//		val pistonInner                         = BlockPistonInner
		val planksAcacia                        = BlockPlanksAcacia
		val planksBigOak                        = BlockPlanksBigOak
		val planksBirch                         = BlockPlanksBirch
		val planksJungle                        = BlockPlanksJungle
		val planksOak                           = BlockPlanksOak
		val planksSpruce                        = BlockPlanksSpruce
//		val portal                              = BlockPortal
		val prismarineBricks                    = BlockPrismarineBricks
		val prismarineDark                      = BlockPrismarineDark
		val prismarineRough                     = BlockPrismarineRough
//		val pumpkinFaceOff                      = BlockPumpkinFaceOff
//		val pumpkinFaceOn                       = BlockPumpkinFaceOn
//		val pumpkinStemConnected                = BlockPumpkinStemConnected
//		val pumpkinStemDisconnected             = BlockPumpkinStemDisconnected
		val purpurBlock                         = BlockPurpurBlock
//		val purpurPillar                        = BlockPurpurPillar
//		val quartzBlockChiseled                 = BlockQuartzBlockChiseled
//		val quartzBlockLines                    = BlockQuartzBlockLines
		val quartzOre                           = BlockQuartzOre
//		val railActivator                       = BlockRailActivator
//		val railActivatorPowered                = BlockRailActivatorPowered
//		val railDetector                        = BlockRailDetector
//		val railDetectorPowered                 = BlockRailDetectorPowered
//		val railGolden                          = BlockRailGolden
//		val railGoldenPowered                   = BlockRailGoldenPowered
//		val railNormal                          = BlockRailNormal
//		val railNormalTurned                    = BlockRailNormalTurned
		val redstoneBlock                       = BlockRedstoneBlock
//		val redstoneDustDot                     = BlockRedstoneDustDot
//		val redstoneDustOverlay                 = BlockRedstoneDustOverlay
//		val redstoneLampOff                     = BlockRedstoneLampOff
//		val redstoneLampOn                      = BlockRedstoneLampOn
		val redstoneOre                         = BlockRedstoneOre
//		val redstoneTorchOff                    = BlockRedstoneTorchOff
//		val redstoneTorchOn                     = BlockRedstoneTorchOn
		val redNetherBrick                      = BlockRedNetherBrick
		val redSand                             = BlockRedSand
//		val redSandstoneCarved                  = BlockRedSandstoneCarved
//		val redSandstoneNormal                  = BlockRedSandstoneNormal
//		val redSandstoneSmooth                  = BlockRedSandstoneSmooth
//		val reeds                               = BlockReeds
//		val repeaterOff                         = BlockRepeaterOff
//		val repeaterOn                          = BlockRepeaterOn
//		val repeatingCommandBlockBack           = BlockRepeatingCommandBlockBack
//		val repeatingCommandBlockConditional    = BlockRepeatingCommandBlockConditional
//		val repeatingCommandBlockFront          = BlockRepeatingCommandBlockFront
		val sand                                = BlockSand
//		val sandstoneCarved                     = BlockSandstoneCarved
//		val sandstoneNormal                     = BlockSandstoneNormal
//		val sandstoneSmooth                     = BlockSandstoneSmooth
//		val saplingAcacia                       = BlockSaplingAcacia
//		val saplingBirch                        = BlockSaplingBirch
//		val saplingJungle                       = BlockSaplingJungle
//		val saplingOak                          = BlockSaplingOak
//		val saplingRoofedOak                    = BlockSaplingRoofedOak
//		val saplingSpruce                       = BlockSaplingSpruce
//		val seaLantern                          = BlockSeaLantern
//		val slime                               = BlockSlime
		val snow                                = BlockSnow
		val soulSand                            = BlockSoulSand
		val sponge                              = BlockSponge
//		val spongeWet                           = BlockSpongeWet
		val stone                               = BlockStone
		val stonebrick                          = BlockStonebrick
//		val stonebrickCarved                    = BlockStonebrickCarved
//		val stonebrickCracked                   = BlockStonebrickCracked
//		val stonebrickMossy                     = BlockStonebrickMossy
//		val stoneAndesite                       = BlockStoneAndesite
//		val stoneAndesiteSmooth                 = BlockStoneAndesiteSmooth
//		val stoneDiorite                        = BlockStoneDiorite
//		val stoneDioriteSmooth                  = BlockStoneDioriteSmooth
//		val stoneGranite                        = BlockStoneGranite
//		val stoneGraniteSmooth                  = BlockStoneGraniteSmooth
//		val structureBlock                      = BlockStructureBlock
//		val structureBlockCorner                = BlockStructureBlockCorner
//		val structureBlockData                  = BlockStructureBlockData
//		val structureBlockLoad                  = BlockStructureBlockLoad
//		val structureBlockSave                  = BlockStructureBlockSave
//		val tallgrass                           = BlockTallgrass
//		val torchOn                             = BlockTorchOn
//		val trapdoor                            = BlockTrapdoor
//		val tripWire                            = BlockTripWire
//		val tripWireSource                      = BlockTripWireSource
//		val vine                                = BlockVine
//		val waterlily                           = BlockWaterlily
//		val waterFlow                           = BlockWaterFlow
//		val waterOverlay                        = BlockWaterOverlay
//		val waterStill                          = BlockWaterStill
//		val web                                 = BlockWeb
//		val woolColoredBlack                    = BlockWoolColoredBlack
//		val woolColoredBlue                     = BlockWoolColoredBlue
//		val woolColoredBrown                    = BlockWoolColoredBrown
//		val woolColoredCyan                     = BlockWoolColoredCyan
//		val woolColoredGray                     = BlockWoolColoredGray
//		val woolColoredGreen                    = BlockWoolColoredGreen
//		val woolColoredLightBlue                = BlockWoolColoredLightBlue
//		val woolColoredLime                     = BlockWoolColoredLime
//		val woolColoredMagenta                  = BlockWoolColoredMagenta
//		val woolColoredOrange                   = BlockWoolColoredOrange
//		val woolColoredPink                     = BlockWoolColoredPink
//		val woolColoredPurple                   = BlockWoolColoredPurple
//		val woolColoredRed                      = BlockWoolColoredRed
//		val woolColoredSilver                   = BlockWoolColoredSilver
//		val woolColoredWhite                    = BlockWoolColoredWhite
//		val woolColoredYellow                   = BlockWoolColoredYellow

	}
}