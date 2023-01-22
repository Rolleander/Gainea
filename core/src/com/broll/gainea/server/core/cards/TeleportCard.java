package com.broll.gainea.server.core.cards;

import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public abstract class TeleportCard extends Card {
    public TeleportCard(int picture, String title, String text) {
        super(picture, title, text);
    }

    public abstract List<? extends Location> getTeleportTargets(Location from);

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Location from = selectHandler.selectLocation("Wählt eine Truppe die bewegt werden soll", owner.getControlledLocations());
        //filter target locations to empty or locations controlled by the player (cant teleport into enemy location)
        List<? extends Location> targets = getTeleportTargets(from).stream().filter(it -> LocationUtils.emptyOrControlled(it, owner)).collect(Collectors.toList());
        if (!targets.isEmpty()) {
            Location to = selectHandler.selectLocation("Wählt ein Reiseziel", targets);
            UnitControl.move(game, PlayerUtils.getUnits(owner, from), to);
        }
    }
}
