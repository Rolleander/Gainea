package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.utils.LocationUtils

enum class MonsterMotion(private val canMove: (Location, Location) -> Boolean) {
    TERRESTRIAL({ from, to -> to !is Ship && from !is Ship && !LocationUtils.isAreaType(to, AreaType.LAKE) }),
    AIRBORNE({ _, _ -> true }),
    AQUARIAN({ _, to -> to is Ship || LocationUtils.isAreaType(to, AreaType.LAKE) }),
    AMPHIBIAN({ _, to -> to is Ship || !LocationUtils.isAreaType(to, AreaType.DESERT) });

    fun canMoveTo(currentLocation: Location, newLocation: Location) =
            newLocation.traversable && canMove(currentLocation, newLocation)

}
