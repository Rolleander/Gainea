package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaCollection
import com.broll.gainea.server.core.map.Expansion
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship

fun Ship.targetLocation(): Area {
    var to: Location = this
    do {
        to = (to as Ship).to
    } while (to is Ship)
    return to as Area
}

fun Ship.sourceLocation(): Area {
    var from: Location = this
    do {
        from = (from as Ship).from
    } while (from is Ship)
    return from as Area
}

fun Ship.leadsTo(area: Area) = targetLocation() == area

fun Ship.leadsTo(areaCollection: AreaCollection) = targetLocation().container == areaCollection

fun Ship.leadsTo(expansion: Expansion) = targetLocation().container.expansion == expansion

fun Ship.startsFrom(area: Area) = sourceLocation() == area

fun Ship.startsFrom(areaCollection: AreaCollection) = sourceLocation().container == areaCollection


fun Ship.startsFrom(expansion: Expansion) = sourceLocation().container.expansion == expansion

fun Ship.connects(from: Area, to: Area) = startsFrom(from) && leadsTo(to)


fun Ship.connects(from: AreaCollection, to: AreaCollection) = startsFrom(from) && leadsTo(to)


fun Ship.connects(from: Expansion, to: Expansion) = startsFrom(from) && leadsTo(to)


fun AreaCollection.getAllShips(to: AreaCollection) =
        ships.filter { it.leadsTo(to) }

fun AreaCollection.getAllShips() =
        listOf(ships,
                expansion.contents.filter { it != this }.flatMap { it.getAllShips(this) }
        ).flatten()


