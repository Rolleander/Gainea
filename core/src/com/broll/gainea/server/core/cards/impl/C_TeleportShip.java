package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractTeleportCard;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_TeleportShip extends AbstractTeleportCard {
    public C_TeleportShip() {
        super(31, "Seeleute", "Bewegt eine Truppe zu einem beliebigen Schiff auf der gleichen Karte");
    }

    @Override
    public List<? extends Location> getTeleportTargets(Location from) {
        return from.getContainer().getExpansion().getAllShips();
    }
}
