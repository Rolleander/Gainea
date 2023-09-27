package com.broll.gainea.server.core.map

import com.broll.gainea.server.init.ExpansionSettingimport

java.util.function.Consumerimport java.util.stream.Collectors
open class MapContainer(val expansionSetting: ExpansionSetting?) {
    var expansions: List<Expansion?>? = null
        protected set
    private val locations: MutableMap<Int, Location?> = HashMap()

    init {
        init(expansionSetting)
        expansions!!.stream().flatMap { it: Expansion? -> it.getAllLocations().stream() }.forEach { l: Location? -> locations[l.getNumber()] = l }
    }

    protected open fun init(setting: ExpansionSetting?) {
        expansions = MapFactory.createRenderless(setting)
    }

    fun getLocation(number: Int): Location? {
        return locations[number]
    }

    val activeExpansionTypes: List<ExpansionType?>
        get() = expansions!!.stream().map { obj: Expansion? -> obj.getType() }.collect(Collectors.toList())
    val allAreas: List<Area?>
        get() {
            val areas: MutableList<Area?> = ArrayList()
            expansions!!.forEach(Consumer { e: Expansion? -> e.getContents().stream().map { obj: AreaCollection? -> obj.getAreas() }.forEach { collection: List<Area?>? -> areas.addAll(collection!!) } })
            return areas
        }
    val allLocations: List<Location?>
        get() {
            val areas: MutableList<Location?> = ArrayList()
            expansions!!.forEach(Consumer { e: Expansion? -> e.getAllLocations().forEach(Consumer { e: Location? -> areas.add(e) }) })
            return areas
        }
    val allShips: List<Ship?>
        get() {
            val ships: MutableList<Ship?> = ArrayList()
            expansions!!.forEach(Consumer { e: Expansion? -> e.getAllShips().forEach(Consumer { e: Ship? -> ships.add(e) }) })
            return ships
        }

    fun getExpansion(type: ExpansionType): Expansion? {
        return expansions!!.stream().filter { it: Expansion? -> it.getType() == type }.findFirst().orElse(null)
    }

    val allIslands: List<Island?>
        get() {
            val islands: MutableList<Island?> = ArrayList()
            expansions!!.forEach(Consumer { e: Expansion? -> e.getContents().stream().filter { it: AreaCollection? -> it is Island }.map { it: AreaCollection? -> it as Island? }.forEach { e: Island? -> islands.add(e) } })
            return islands
        }
    val allContinents: List<Continent?>
        get() {
            val continents: MutableList<Continent?> = ArrayList()
            expansions!!.forEach(Consumer { e: Expansion? -> e.getContents().stream().filter { it: AreaCollection? -> it is Continent }.map { it: AreaCollection? -> it as Continent? }.forEach { e: Continent? -> continents.add(e) } })
            return continents
        }
    val allContainers: List<AreaCollection?>
        get() {
            val containers: MutableList<AreaCollection?> = ArrayList()
            expansions!!.forEach(Consumer { e: Expansion? -> e.getContents().forEach(Consumer { e: AreaCollection? -> containers.add(e) }) })
            return containers
        }

    fun getArea(id: AreaID): Area? {
        return allAreas.stream().filter { it: Area? -> it.getId() === id }.findFirst().orElse(null)
    }

    fun getIsland(id: IslandID): Island? {
        return allIslands.stream().filter { it: Island? -> it.getId() === id }.findFirst().orElse(null)
    }

    fun getContinent(id: ContinentID): Continent? {
        return allContinents.stream().filter { it: Continent? -> it.getId() === id }.findFirst().orElse(null)
    }
}
