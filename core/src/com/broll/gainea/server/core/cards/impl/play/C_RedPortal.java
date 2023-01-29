package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class C_RedPortal extends Card {

    public C_RedPortal() {
        super(1, "Dunkles Portal",
                "Wählt ein besetztes Feld, alle Einheiten darauf werden auf ein zufälliges freies Land der gleichen Karte teleportiert.");
        setDrawChance(0.6f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Location from = selectHandler.selectLocation("Welche Einheiten teleportieren?", game.getMap().getAllLocations().stream().filter(it -> !it.isFree()).collect(Collectors.toList()));
        Location to = LocationUtils.getRandomFree(from.getContainer().getExpansion().getAllAreas());
        if (to != null) {
            UnitControl.move(game, new ArrayList<>(from.getInhabitants()), to);
        }
    }

}
