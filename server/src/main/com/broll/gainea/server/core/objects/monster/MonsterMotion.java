package com.broll.gainea.server.core.objects.monster;

import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.function.BiFunction;

public enum MonsterMotion {

    TERRESTRIAL((from, to) -> !(to instanceof Ship) && !(from instanceof Ship) && !LocationUtils.isAreaType(to, AreaType.LAKE)),
    AIRBORNE((from, to) -> true),
    AQUARIAN((from, to) -> (to instanceof Ship) || LocationUtils.isAreaType(to, AreaType.LAKE)),
    AMPHIBIAN((from, to) -> (to instanceof Ship) || !LocationUtils.isAreaType(to, AreaType.DESERT));

    private BiFunction<Location, Location, Boolean> canMove;

    MonsterMotion(BiFunction<Location, Location, Boolean> canMove) {
        this.canMove = canMove;
    }

    public boolean canMoveTo(Location currentLocation, Location newLocation) {
        return newLocation.isTraversable() && this.canMove.apply(currentLocation, newLocation);
    }

}
