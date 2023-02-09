package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class C_TeleportGate extends Card {
    public C_TeleportGate() {
        super(56, "Teleporter", "Teleportiert eine eurer Armeen auf ein beliebiges freies Land der gleichen Karte");
        setDrawChance(0.3f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Location from = selectHandler.selectLocation("Welche Armee soll teleportiert werden?", owner.getControlledLocations());
        List<Location> locations = from.getContainer().getExpansion().getAllAreas().stream().filter(Location::isFree).collect(Collectors.toList());
        Location target = selectHandler.selectLocation("Wohin?", locations);
        UnitControl.move(game, PlayerUtils.getUnits(owner, from), target);
    }

}
