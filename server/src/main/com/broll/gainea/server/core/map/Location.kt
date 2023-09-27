package com.broll.gainea.server.core.map

import com.broll.gainea.server.core.objects.MapObject


abstract class Location {

    var number = 0

    var container: AreaCollection? = null
    var isTraversable = true

    var coordinates: Coordinates? = null
    val inhabitants = mutableSetOf<MapObject>()
    abstract val connectedLocations: MutableSet<Location>
    val isFree: Boolean
        get() = inhabitants.isEmpty() && isTraversable
    val walkableNeighbours: List<Location>
        get() = connectedLocations.filter { it.isTraversable }.filter {
            if (it is Ship) {
                return@filter it.passable(this)
            }
            true
        }
}
