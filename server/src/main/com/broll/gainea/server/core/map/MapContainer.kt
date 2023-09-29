package com.broll.gainea.server.core.map

import com.broll.gainea.server.init.ExpansionSetting

open class MapContainer(val expansionSetting: ExpansionSetting) {
    val expansions = mutableListOf<Expansion>()
    private val locations: MutableMap<Int, Location> = HashMap()

    init {
        init(expansionSetting)
        expansions.flatMap { it.allLocations }.forEach { locations[it.number] = it }
    }

    protected open fun init(setting: ExpansionSetting) {
        expansions += MapFactory.createRenderless(setting)
    }

    fun getLocation(number: Int) = locations[number]!!

    val activeExpansionTypes: List<ExpansionType>
        get() = expansions.map { it.type }

    val allAreas: List<Area>
        get() = expansions.flatMap { it.allAreas }

    val allLocations: List<Location>
        get() = expansions.flatMap { it.allLocations }
    val allShips: List<Ship>
        get() = expansions.flatMap { it.allShips }

    val allIslands: List<Island>
        get() = expansions.flatMap { it.islands }
    val allContinents: List<Continent>
        get() = expansions.flatMap { it.continents }

    val allContainers: List<AreaCollection>
        get() = expansions.flatMap { it.contents }

    fun getExpansion(type: ExpansionType) = expansions.find { it.type == type }

    fun getArea(id: AreaID) = allAreas.find { it.id == id }

    fun getIsland(id: IslandID) = allIslands.find { it.id == id }

    fun getContinent(id: ContinentID) = allContinents.find { it.id == id }
}
