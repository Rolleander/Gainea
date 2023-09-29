package com.broll.gainea.server.core.map

abstract class ExpansionFactory(val type: ExpansionType, val texture: String) {
    private val expansion: Expansion
    protected val baseCoordinates: Coordinates = Coordinates(0f, 0f)
    private val connectedExpansions = mutableListOf<ExpansionFactory>()
    private val areas = HashMap<AreaID, Area>()
    private val continents = HashMap<ContinentID, Continent>()
    private val islands = HashMap<IslandID, Island>()

    init {
        expansion = Expansion(type, baseCoordinates)
    }

    private fun coord(x: Float, y: Float): Coordinates {
        val c = Coordinates(x / 100f, y / 100f)
        c.shift(baseCoordinates.x, baseCoordinates.y)
        return c
    }

    protected fun connect(vararg areas: AreaID) {
        for (a1 in areas) {
            for (a2 in areas) {
                if (a1 !== a2) {
                    val area1 = get(a1)
                    val area2 = get(a2)
                    area1.addAdjacentLocation(area2)
                }
            }
        }
    }

    protected abstract fun init()
    protected abstract fun connectWithExpansion(expansion: ExpansionFactory)
    fun connectExpansion(expansion: ExpansionFactory) {
        connectedExpansions.add(expansion)
        connectWithExpansion(expansion)
    }

    protected fun island(id: IslandID, name: String, areaIds: List<AreaID>): IslandID {
        val island = Island(name, id)
        val areas = areaIds.map { areas[it]!! }
        areas.forEach { it.container = island }
        island.areas += areas
        islands[id] = island
        return id
    }

    protected fun continent(id: ContinentID, name: String, areaIds: List<AreaID>): ContinentID {
        val continent = Continent(name, id)
        val areas = areaIds.map { areas[it]!! }
        areas.forEach { it.container = continent }
        continent.areas += areas
        continents[id] = continent
        return id
    }

    protected fun area(id: AreaID, name: String, type: AreaType, x: Float, y: Float): AreaID {
        val area = Area(id, type, name, coord(x, y))
        areas[id] = area
        return id
    }

    private fun ship(from: Location, to: Location, x: Float, y: Float): Ship {
        val ship = Ship(coord(x, y))
        ship.from = from
        ship.to = to
        if (from is Area) {
            from.addAdjacentLocation(ship)
        }
        if (to is Area) {
            to.addAdjacentLocation(ship)
        }
        return ship
    }

    protected fun ship(from: AreaID, to: AreaID, x: Float, y: Float): Ship {
        val ship = ship(get(from), get(to), x, y)
        get(from).container.ships.add(ship)
        ship.container = get(from).container
        return ship
    }

    protected fun ships(fromId: AreaID, toId: AreaID, x: FloatArray, y: FloatArray): List<Ship> {
        val ships = mutableListOf<Ship>()
        var current: Location = get(fromId)
        val to = get(toId)
        val container = get(fromId).container
        for (i in x.indices) {
            val ship = Ship(coord(x[i], y[i]))
            ship.from = current
            ship.container = container
            ships.add(ship)
            current = ships[i]
        }
        for (i in x.indices) {
            var next: Location = to
            if (i < x.size - 1) {
                next = ships[i + 1]
            } else {
                to.addAdjacentLocation(ships[i])
            }
            ships[i].to = next
        }
        container.ships.addAll(ships)
        return ships
    }

    fun create(): Expansion {
        init()
        expansion.contents += continents.values
        expansion.contents += islands.values
        islands.values.forEach { it.init(expansion) }
        continents.values.forEach { it.init(expansion) }
        return expansion
    }

    private operator fun get(id: AreaID): Area {
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
        throw RuntimeException("unknown id")
    }

    private operator fun get(id: ContinentID): Continent {
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
        throw RuntimeException("unknown id")
    }

    private operator fun get(id: IslandID): Island {
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
        throw RuntimeException("unknown id")
    }
}
