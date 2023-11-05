package com.broll.gainea.server.core.map

import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.getUnits


abstract class Location(val coordinates: Coordinates) {

    var number = 0

    lateinit var container: AreaCollection
    var traversable = true

    val inhabitants = mutableSetOf<MapObject>()
    val connectedLocations = mutableSetOf<Location>()

    val free: Boolean
        get() = inhabitants.isEmpty() && traversable
    val walkableNeighbours: List<Location>
        get() = connectedLocations.filter { it.traversable }.filter {
            if (it is Ship) {
                return@filter it.goesFrom(this)
            }
            true
        }

    val units: List<Unit>
        get() = inhabitants.toList().getUnits()
}
