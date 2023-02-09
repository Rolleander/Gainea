package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.TeleportCard;
import com.broll.gainea.server.core.map.Location;

import java.util.List;

public class C_TeleportShip extends TeleportCard {
    public C_TeleportShip() {
        super(31, "Seeleute", "Bewegt eine Truppe zu einem beliebigen Schiff auf der gleichen Karte");
    }

    @Override
    public List<? extends Location> getTeleportTargets(Location from) {
        return from.getContainer().getExpansion().getAllShips();
    }
}
