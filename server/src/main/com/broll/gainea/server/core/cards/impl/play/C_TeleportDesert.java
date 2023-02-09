package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.TeleportCard;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_TeleportDesert extends TeleportCard {
    public C_TeleportDesert() {
        super(48, "Wüstenkarawane", "Bewegt eine Truppe zu einer beliebigen Wüste auf der gleichen Karte");
    }

    @Override
    public List<Location> getTeleportTargets(Location from) {
        return LocationUtils.filterByType(from.getContainer().getExpansion().getAllLocations(), AreaType.DESERT).collect(Collectors.toList());
    }
}
