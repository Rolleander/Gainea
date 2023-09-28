package com.broll.gainea.server.core.map

import java.util.stream.Collectors

class Expansion {
    @JvmField
    var coordinates: Coordinates? = null

    @JvmField
    var contents: List<AreaCollection?> = ArrayList()
    var type: ExpansionType? = null
    val allLocations: List<Location>
        get() {
            val locations: MutableList<Location?> = ArrayList()
            contents.stream().map { obj: AreaCollection? -> obj.getAreas() }.forEach { collection: List<Area?>? -> locations.addAll(collection!!) }
            contents.stream().map { obj: AreaCollection? -> obj.getShips() }.forEach { collection: List<Ship?>? -> locations.addAll(collection!!) }
            return locations
        }
    val allAreas: List<Area>
        get() {
            val locations: MutableList<Area?> = ArrayList()
            contents.stream().map { obj: AreaCollection? -> obj.getAreas() }.forEach { collection: List<Area?>? -> locations.addAll(collection!!) }
            return locations
        }
    val allShips: List<Ship>
        get() {
            val locations: MutableList<Ship?> = ArrayList()
            contents.stream().map { obj: AreaCollection? -> obj.getShips() }.forEach { collection: List<Ship?>? -> locations.addAll(collection!!) }
            return locations
        }
    val islands: List<Island?>
        get() = contents.stream().filter { it: AreaCollection? -> it is Island }.map { it: AreaCollection? -> it as Island? }.collect(Collectors.toList())

    fun getIsland(id: IslandID): Island? {
        return islands.stream().filter { it: Island? -> it.getId() === id }.findFirst().orElse(null)
    }

    fun getContinent(id: ContinentID): Continent? {
        return continents.stream().filter { it: Continent? -> it.getId() === id }.findFirst().orElse(null)
    }

    val continents: List<Continent?>
        get() = contents.stream().filter { it: AreaCollection? -> it is Continent }.map { it: AreaCollection? -> it as Continent? }.collect(Collectors.toList())
}
