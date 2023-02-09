package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class C_ResurrectCommander extends Card {
    public C_ResurrectCommander() {
        super(61, "Auferstehung", "Lasst euren gefallenen Feldherr zurückkehren");
        setDrawChance(2f);
    }

    @Override
    public boolean isPlayable() {
        return !PlayerUtils.isCommanderAlive(owner);
    }

    @Override
    protected void play() {
        List<Location> locations = new ArrayList<>(owner.getControlledLocations());
        if(locations.isEmpty()){
            locations.add(LocationUtils.getRandomFree(game.getMap().getAllAreas()));
        }
        placeUnitHandler.placeCommander(owner, locations);
    }

}
