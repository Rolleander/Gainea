package com.broll.gainea.server.core.map

import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.GameUtils


abstract class Location {

    var number = 0

    lateinit var container: AreaCollection
    var traversable = true

    var coordinates: Coordinates? = null
    val inhabitants = mutableSetOf<MapObject>()
    abstract val connectedLocations: MutableSet<Location>
    val isFree: Boolean
        get() = inhabitants.isEmpty() && traversable
    val walkableNeighbours: List<Location>
        get() = connectedLocations.filter { it.traversable }.filter {
            if (it is Ship) {
                return@filter it.passable(this)
            }
            true
        }

    val units: List<Unit>
        get() = GameUtils.getUnits(inhabitants.toList())
}
