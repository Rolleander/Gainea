package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;

import org.apache.commons.lang3.ArrayUtils;

public final class LocationUtils {

    public static boolean isAreaType(Location location, AreaType... type) {
        if (location instanceof Area) {
            Area area = (Area) location;
            return ArrayUtils.contains(type, area.getType());
        }
        return false;
    }
}
