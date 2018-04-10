package blue.sparse.minecraft.common.biome

import blue.sparse.math.vectors.floats.Vector2f
import blue.sparse.math.vectors.floats.lengthSquared
import blue.sparse.minecraft.common.util.Identifier
import blue.sparse.minecraft.common.util.proxy.Proxy
import blue.sparse.minecraft.common.util.proxy.ProxyProvider

abstract class BiomeType(
		val identifier: Identifier,
		val temperature: Float = 0.5f,
		val rainfall: Float = 0.5f
) {
	open val proxy: BiomeTypeProxy by ProxyProvider<BiomeTypeProxy>(
			"blue.sparse.minecraft.client.biome.proxy.Default",
			"blue.sparse.minecraft.server.biome.proxy.Default",
			this
	)

	constructor(id: String, temperature: Float = 0.5f, rainfall: Float = 0.5f) : this(Identifier(id), temperature, rainfall)

	init {
		register(this)
	}

	companion object {
		internal val registry = LinkedHashMap<Identifier, BiomeType>()

		private fun register(type: BiomeType) {
			if (type.identifier in registry)
				throw IllegalArgumentException("Biome with identifier \"${type.identifier}\" is already registered.")

			registry[type.identifier] = type
		}

		operator fun get(identifier: Identifier) = registry[identifier]

		operator fun get(name: String) = get(Identifier(name))

		operator fun get(temperature: Float, rainfall: Float): BiomeType? {
//			val result = registry.values.sortedBy {
//				it.temperature - temperature
//			}.minBy {
//				it.rainfall - rainfall
//			}
			val result = registry.values.minBy {
				lengthSquared(Vector2f(it.temperature, it.rainfall) - Vector2f(temperature, rainfall))
			}

//			println("$temperature, $rainfall -> ${result?.temperature}, ${result?.rainfall} ${result?.identifier}")
			return result
//			return registry.values.sortedBy { temperature.compareTo(it.temperature) }.minBy { rainfall.compareTo(it.rainfall) }
		}

		val ocean = BiomeOcean
		val plains = BiomePlains
		val desert = BiomeDesert
		val hills = BiomeHills
		val forest = BiomeForest
		val taiga = BiomeTaiga
		val swamp = BiomeSwamp
		val river = BiomeRiver
		val hell = BiomeHell
		val end = BiomeEnd
		val frozenOcean = BiomeFrozenOcean
		val frozenRiver = BiomeFrozenRiver
		val iceFlats = BiomeIceFlats
		val iceMountains = BiomeIceMountains
		val mushroomIsland = BiomeMushroomIsland
		val mushroomIslandShore = BiomeMushroomIslandShore
		val beach = BiomeBeach
		val desertHills = BiomeDesertHills
		val forestHills = BiomeForestHills
		val taigaHills = BiomeTaigaHills
		val smallHills = BiomeSmallHills
		val jungle = BiomeJungle
		val jungleHills = BiomeJungleHills
		val jungleEdge = BiomeJungleEdge
		val deepOcean = BiomeDeepOcean
		val stoneBeach = BiomeStoneBeach
		val coldBeach = BiomeColdBeach
		val forestBirch = BiomeForestBirch
		val forestBirchHills = BiomeForestBirchHills
		val forestRoofed = BiomeForestRoofed
		val taigaCold = BiomeTaigaCold
		val taigaColdHills = BiomeTaigaColdHills
		val taigaMega = BiomeTaigaMega
		val taigaMegaHills = BiomeTaigaMegaHills
		val hillsTrees = BiomeHillsTrees
		val savanna = BiomeSavanna
		val savannaRock = BiomeSavannaRock
		val mesa = BiomeMesa
		val mesaRock = BiomeMesaRock
		val mesaClearRock = BiomeMesaClearRock
		val void = BiomeVoid
		val plainsMutated = BiomePlainsMutated
		val desertMutated = BiomeDesertMutated
		val hillsMutated = BiomeHillsMutated
		val forestMutated = BiomeForestMutated
		val taigaMutated = BiomeTaigaMutated
		val swampMutated = BiomeSwampMutated
		val iceFlatsMutated = BiomeIceFlatsMutated
		val jungleMutated = BiomeJungleMutated
		val jungleEdgeMutated = BiomeJungleEdgeMutated
		val forestBirchMutated = BiomeForestBirchMutated
		val forestBirchHillsMutated = BiomeForestBirchHillsMutated
		val forestRoofedMutated = BiomeForestRoofedMutated
		val taigaColdMutated = BiomeTaigaColdMutated
		val taigaMegaMutated = BiomeTaigaMegaMutated
		val taigaMegaHillsMutated = BiomeTaigaMegaHillsMutated
		val hillsTreesMutated = BiomeHillsTreesMutated
		val savannaMutated = BiomeSavannaMutated
		val savannaRockMutated = BiomeSavannaRockMutated
		val mesaMutated = BiomeMesaMutated
		val mesaRockMutated = BiomeMesaRockMutated
		val mesaClearRockMutated = BiomeMesaClearRockMutated
	}

	abstract class BiomeTypeProxy(val biomeType: BiomeType) : Proxy
}