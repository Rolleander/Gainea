package com.broll.gainea.server.core.map

import java.util.Arraysimport

java.util.function.Consumerimport java.util.stream.Collectors
abstract class ExpansionFactory(type: ExpansionType?) {
    private val expansion: Expansion
    protected var baseCoordinates: Coordinates? = null
    private val connectedExpansions: MutableList<ExpansionFactory> = ArrayList()
    private val areas: MutableMap<AreaID, Area> = HashMap()
    private val continents: MutableMap<ContinentID, Continent?> = HashMap()
    private val islands: MutableMap<IslandID, Island?> = HashMap()

    init {
        expansion = Expansion()
        expansion.type = type
        setBaseCoordinates(0f, 0f)
    }

    protected fun setBaseCoordinates(x: Float, y: Float) {
        baseCoordinates = Coordinates(x, y)
        expansion.coordinates = baseCoordinates
    }

    protected fun coord(x: Float, y: Float): Coordinates {
        val c = Coordinates(x / 100f, y / 100f)
        c.shift(baseCoordinates!!.x, baseCoordinates!!.y)
        return c
    }

    protected fun connect(vararg areas: AreaID) {
        for (a1 in areas) {
            for (a2 in areas) {
                if (a1 !== a2) {
                    val area1 = get(a1)
                    val area2 = get(a2)
                    area1!!.addAdjacentLocation(area2)
                }
            }
        }
    }

    @JvmField
    abstract val texture: String
    protected abstract fun init()
    protected abstract fun connectWithExpansion(expansion: ExpansionFactory?)
    fun connectExpansion(expansion: ExpansionFactory) {
        connectedExpansions.add(expansion)
        connectWithExpansion(expansion)
    }

    protected fun island(id: IslandID, name: String?, areaIds: List<AreaID>): IslandID {
        val island = Island(id)
        val areas = areaIds.stream().map { id: AreaID -> this[id] }.collect(Collectors.toList())
        areas.forEach(Consumer { area: Area? -> area.setContainer(island) })
        island.areas = areas
        island.name = name
        islands[id] = island
        return id
    }

    protected fun continent(id: ContinentID, name: String?, areaIds: List<AreaID>): ContinentID {
        val continent = Continent(id)
        val areas = areaIds.stream().map { id: AreaID -> this[id] }.collect(Collectors.toList())
        areas.forEach(Consumer { area: Area? -> area.setContainer(continent) })
        continent.areas = areas
        continent.name = name
        continents[id] = continent
        return id
    }

    protected fun area(id: AreaID, name: String?, type: AreaType?, x: Float, y: Float): AreaID {
        val area = Area(id)
        area.name = name
        area.type = type
        area.coordinates = coord(x, y)
        areas[id] = area
        return id
    }

    private fun ship(from: Location?, to: Location?, x: Float, y: Float): Ship {
        val ship = Ship()
        ship.from = from
        ship.to = to
        if (from is Area) {
            from.addAdjacentLocation(ship)
        }
        if (to is Area) {
            to.addAdjacentLocation(ship)
        }
        ship.coordinates = coord(x, y)
        return ship
    }

    protected fun ship(from: AreaID, to: AreaID, x: Float, y: Float): Ship {
        val ship = ship(get(from), get(to), x, y)
        get(from).getContainer().ships.add(ship)
        ship.container = get(from).getContainer()
        return ship
    }

    protected fun ships(fromId: AreaID, toId: AreaID, x: FloatArray, y: FloatArray): List<Ship?> {
        val ships: MutableList<Ship?> = ArrayList()
        var current: Location? = get(fromId)
        val to = get(toId)
        val container = get(fromId).getContainer()
        for (i in x.indices) {
            val ship = ship(current, null, x[i], y[i])
            ship.container = container
            ships.add(ship)
            current = ships[i]
        }
        for (i in x.indices) {
            var next: Location? = to
            if (i < x.size - 1) {
                next = ships[i + 1]
            } else {
                to!!.addAdjacentLocation(ships[i])
            }
            ships[i].setTo(next)
        }
        container.ships.addAll(ships)
        return ships
    }

    protected fun <T> list(vararg t: T): List<T> {
        return Arrays.asList(*t)
    }

    fun create(): Expansion {
        val contents: MutableList<AreaCollection?> = ArrayList()
        init()
        contents.addAll(islands.values)
        contents.addAll(continents.values)
        expansion.contents = contents
        islands.values.forEach(Consumer { it: Island? -> it!!.init(expansion) })
        continents.values.forEach(Consumer { it: Continent? -> it!!.init(expansion) })
        return expansion
    }

    private operator fun get(id: AreaID): Area? {
        var area = areas[id]
        if (area != null) {
            return area
        } else {
            for (e in connectedExpansions) {
                area = e.areas[id]
                if (area != null) {
                    return area
                }
            }
        }
        return null
    }

    private operator fun get(id: ContinentID): Continent? {
        var area = continents[id]
        if (area != null) {
            return area
        } else {
            for (e in connectedExpansions) {
                area = e.continents[id]
                if (area != null) {
                    return area
                }
            }
        }
        return null
    }

    private operator fun get(id: IslandID): Island? {
        var area = islands[id]
        if (area != null) {
            return area
        } else {
            for (e in connectedExpansions) {
                area = e.islands[id]
                if (area != null) {
                    return area
                }
            }
        }
        return null
    }
}
