package com.broll.gainea.server.core.objects.monster;

import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.function.Function;

public enum MonsterMotion {

    TERRESTRIAL(to -> !(to instanceof Ship) && !LocationUtils.isAreaType(to, AreaType.LAKE)),
    AIRBORNE(to -> true),
    AQUARIAN(to -> (to instanceof Ship) || LocationUtils.isAreaType(to, AreaType.LAKE)),
    AMPHIBIAN(to -> !LocationUtils.isAreaType(to, AreaType.DESERT));

    private Function<Location, Boolean> canMove;

    MonsterMotion(Function<Location, Boolean> canMove) {
        this.canMove = canMove;
    }

    public boolean canMoveTo(Location location) {
        return location.isTraversable() && this.canMove.apply(location);
    }

}
