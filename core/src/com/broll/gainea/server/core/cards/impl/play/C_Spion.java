package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.Set;

public class C_Spion extends Card {
    public C_Spion() {
        super(8, "Spion", "Platziert einen Soldat auf ein besetztes Land eines beliebigen Spielers ohne einen Kampf.");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Set<Location> locations = PlayerUtils.getHostileLocations(game, owner);
        placeUnitHandler.placeSoldier(owner, new ArrayList<>(locations));
    }

}
