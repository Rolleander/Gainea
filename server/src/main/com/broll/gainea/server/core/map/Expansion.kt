package com.broll.gainea.server.core.map

class Expansion(val type: ExpansionType,
                val coordinates: Coordinates
) {
    val contents = mutableListOf<AreaCollection>()
    val allLocations: List<Location>
        get() = listOf(allAreas, allShips).flatten()
    val allAreas: List<Area>
        get() = contents.flatMap { it.areas }
    val allShips: List<Ship>
        get() = contents.flatMap { it.ships }
    val islands: List<Island>
        get() = contents.filterIsInstance(Island::class.java)

    val continents: List<Continent>
        get() = contents.filterIsInstance(Continent::class.java)

    fun getIsland(id: IslandID) = islands.find { it.id == id }

    fun getContinent(id: ContinentID) = continents.find { it.id == id }

}
