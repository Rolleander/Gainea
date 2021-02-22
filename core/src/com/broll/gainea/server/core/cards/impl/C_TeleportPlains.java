package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractTeleportCard;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_TeleportPlains extends AbstractTeleportCard {
    public C_TeleportPlains() {
        super(43, "Steppentrip", "Bewegt eine Truppe zu einer beliebigen Steppe auf der gleichen Karte");
    }

    @Override
    public List<Location> getTeleportTargets(Location from) {
        return LocationUtils.filterByType(from.getContainer().getExpansion().getAllLocations(), AreaType.PLAINS).collect(Collectors.toList());
    }
}
