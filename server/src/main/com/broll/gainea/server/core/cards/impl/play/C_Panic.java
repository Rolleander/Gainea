package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.utils.UnitControl;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class C_Panic extends Card {
    public C_Panic() {
        super(46, "Massenpanik", "Wählt ein Land mit mindestens einer Einheit. Alle Einheiten des gewählten Ortes werden auf angrenzende Orte verteilt.");
        setDrawChance(0.9f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        List<Location> locations = game.getMap().getAllAreas().stream().filter(it -> !it.getInhabitants().isEmpty()).collect(Collectors.toList());
        if (!locations.isEmpty()) {
            Location source = selectHandler.selectLocation("Wählt einen Zielort für die Panik", locations);
            List<MapObject> inhabitants = new ArrayList<>(source.getInhabitants());
            List<Location> neighbours = new ArrayList<>(source.getConnectedLocations());
            Collections.shuffle(neighbours);
            int index = 0;
            for (MapObject object : inhabitants) {
                Location target = neighbours.get(index);
                UnitControl.move(game, Lists.newArrayList(object), target);
                index++;
                if (index >= neighbours.size()) {
                    index = 0;
                }
            }
        }
    }

}
