package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractTeleportCard;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_TeleportLake extends AbstractTeleportCard {
    public C_TeleportLake() {
        super(48, "Küstenreise", "Bewegt eine Truppe zu einem beliebigem Meer auf der gleichen Karte");
    }

    @Override
    public List<Location> getTeleportTargets(Location from) {
        return LocationUtils.filterByType(from.getContainer().getExpansion().getAllLocations(), AreaType.LAKE).collect(Collectors.toList());
    }
}
