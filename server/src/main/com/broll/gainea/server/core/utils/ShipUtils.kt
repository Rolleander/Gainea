package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaCollection
import com.broll.gainea.server.core.map.Expansion
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship
import java.util.stream.Collectors

object ShipUtils {
    fun targetLocation(ship: Ship?): Area? {
        var to: Location? = ship
        do {
            to = (to as Ship?).getTo()
        } while (to is Ship)
        return to as Area?
    }

    fun sourceLocation(ship: Ship?): Area? {
        var from: Location? = ship
        do {
            from = (from as Ship?).getFrom()
        } while (from is Ship)
        return from as Area?
    }

    fun leadsTo(ship: Ship?, area: Area): Boolean {
        val to = targetLocation(ship)
        return to.getNumber() == area.number
    }

    fun leadsTo(ship: Ship?, areaCollection: AreaCollection?): Boolean {
        val to = targetLocation(ship)
        return to.getContainer() === areaCollection
    }

    fun leadsTo(ship: Ship?, expansion: Expansion): Boolean {
        val to = targetLocation(ship)
        return to.getContainer().expansion === expansion
    }

    fun startsFrom(ship: Ship?, area: Area): Boolean {
        val from = sourceLocation(ship)
        return from.getNumber() == area.number
    }

    fun startsFrom(ship: Ship?, areaCollection: AreaCollection): Boolean {
        val from = sourceLocation(ship)
        return from.getContainer() === areaCollection
    }

    fun startsFrom(ship: Ship?, expansion: Expansion): Boolean {
        val from = sourceLocation(ship)
        return from.getContainer().expansion === expansion
    }

    fun connects(ship: Ship?, from: Area, to: Area): Boolean {
        return startsFrom(ship, from) && leadsTo(ship, to)
    }

    fun connects(ship: Ship?, from: AreaCollection, to: AreaCollection?): Boolean {
        return startsFrom(ship, from) && leadsTo(ship, to)
    }

    fun connects(ship: Ship?, from: Expansion, to: Expansion): Boolean {
        return startsFrom(ship, from) && leadsTo(ship, to)
    }

    fun getAllShips(from: AreaCollection?, to: AreaCollection?): List<Ship?> {
        return from.getShips().stream().filter { it: Ship? -> leadsTo(it, to) }.collect(Collectors.toList())
    }

    fun getAllShips(fromAndTo: AreaCollection?): List<Ship?> {
        val ships: MutableList<Ship?> = ArrayList()
        ships.addAll(fromAndTo.getShips())
        fromAndTo.getExpansion().contents.stream().filter { it: AreaCollection? -> it !== fromAndTo }.forEach { collection: AreaCollection? -> ships.addAll(getAllShips(collection, fromAndTo)) }
        return ships
    }
}
