package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.AbstractTeleportCard;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_TeleportSwamp extends AbstractTeleportCard {
    public C_TeleportSwamp() {
        super(45, "Sumpferkundung", "Bewegt eine Truppe zu einem beliebigen Sumpf auf der gleichen Karte");
    }

    @Override
    public List<Location> getTeleportTargets(Location from) {
        return LocationUtils.filterByType(from.getContainer().getExpansion().getAllLocations(), AreaType.BOG).collect(Collectors.toList());
    }
}
