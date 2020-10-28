package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractTeleportCard;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_TeleportSnow extends AbstractTeleportCard {
    public C_TeleportSnow() {
        super(16, "Polarexpedition", "Bewegt eine Truppe zu einem beliebigem Eisland auf der gleichen Karte");
    }

    @Override
    public List<Location> getTeleportTargets(Location from) {
        return LocationUtils.filterByType(from.getContainer().getExpansion().getAllLocations(), AreaType.SNOW).collect(Collectors.toList());
    }
}
