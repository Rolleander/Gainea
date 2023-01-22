package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.TeleportCard;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_TeleportMountain extends TeleportCard {
    public C_TeleportMountain() {
        super(44, "Bergwanderung", "Bewegt eine Truppe zu einem beliebigen Berg auf der gleichen Karte");
    }

    @Override
    public List<Location> getTeleportTargets(Location from) {
        return LocationUtils.filterByType(from.getContainer().getExpansion().getAllLocations(), AreaType.MOUNTAIN).collect(Collectors.toList());
    }
}
