package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.utils.PlayerUtils;

public class C_AirDrop extends DirectlyPlayedCard {
    public C_AirDrop() {
        super(7, "Verstärkung", "Jeder Spieler platziert einen Soldat");
    }

    @Override
    protected void play() {
        PlayerUtils.iteratePlayers(game, 1000, player -> {
            placeUnitHandler.placeSoldier(player, player.getControlledLocations());
        });
    }

}
