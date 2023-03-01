package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class C_Tunnel extends Card {

    private final static int DISTANCE = 4;

    public C_Tunnel() {
        super(77, "Tunnelgräber", "Bewegt eine Truppe auf ein beliebiges freies Gebiet, welches maximal " + DISTANCE + " Schritte entfernt ist");
    }

    @Override
    public boolean isPlayable() {
        return !owner.getUnits().isEmpty();
    }

    @Override
    protected void play() {
        Location from = selectHandler.selectLocation("Wählt eine Truppe", owner.getControlledLocations());
        List<Location> targets = LocationUtils.getWalkableLocations(owner, from, DISTANCE).stream()
                .filter(it -> it.isFree() && !(it instanceof Ship)).collect(Collectors.toList());
        Location to = selectHandler.selectLocation("Wählt das Reiseziel", targets);
        UnitControl.move(game, PlayerUtils.getUnits(owner, from), to);
    }

}
