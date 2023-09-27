package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.utils.LocationUtils
import java.util.function.BiFunction

enum class MonsterMotion(private val canMove: BiFunction<Location?, Location?, Boolean>) {
    TERRESTRIAL(BiFunction { from: Location?, to: Location? -> to !is Ship && from !is Ship && !LocationUtils.isAreaType(to, AreaType.LAKE) }),
    AIRBORNE(BiFunction { from: Location?, to: Location? -> true }),
    AQUARIAN(BiFunction { from: Location?, to: Location? -> to is Ship || LocationUtils.isAreaType(to, AreaType.LAKE) }),
    AMPHIBIAN(BiFunction { from: Location?, to: Location? -> to is Ship || !LocationUtils.isAreaType(to, AreaType.DESERT) });

    fun canMoveTo(currentLocation: Location?, newLocation: Location?): Boolean {
        return newLocation!!.isTraversable && canMove.apply(currentLocation, newLocation)
    }
}
