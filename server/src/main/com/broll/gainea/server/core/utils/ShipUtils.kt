package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaCollection
import com.broll.gainea.server.core.map.Expansion
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship

object ShipUtils {
    fun targetLocation(ship: Ship): Area {
        var to: Location = ship
        do {
            to = (to as Ship).to
        } while (to is Ship)
        return to as Area
    }

    fun sourceLocation(ship: Ship): Area {
        var from: Location = ship
        do {
            from = (from as Ship).from
        } while (from is Ship)
        return from as Area
    }

    fun leadsTo(ship: Ship, area: Area) = targetLocation(ship) == area

    fun leadsTo(ship: Ship, areaCollection: AreaCollection) = targetLocation(ship).container == areaCollection

    fun leadsTo(ship: Ship, expansion: Expansion) = targetLocation(ship).container.expansion == expansion

    fun startsFrom(ship: Ship, area: Area) = sourceLocation(ship) == area

    fun startsFrom(ship: Ship, areaCollection: AreaCollection) = sourceLocation(ship).container == areaCollection


    fun startsFrom(ship: Ship, expansion: Expansion) = sourceLocation(ship).container.expansion == expansion

    fun connects(ship: Ship, from: Area, to: Area) = startsFrom(ship, from) && leadsTo(ship, to)


    fun connects(ship: Ship, from: AreaCollection, to: AreaCollection) = startsFrom(ship, from) && leadsTo(ship, to)


    fun connects(ship: Ship, from: Expansion, to: Expansion) = startsFrom(ship, from) && leadsTo(ship, to)


    fun getAllShips(from: AreaCollection, to: AreaCollection) =
            from.ships.filter { leadsTo(it, to) }

    fun getAllShips(fromAndTo: AreaCollection) =
            listOf(fromAndTo.ships,
                    fromAndTo.expansion.contents.filter { it != fromAndTo }.map { getAllShips(it, fromAndTo) }
            ).flatten()

}
