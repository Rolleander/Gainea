package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.utils.PlayerUtils;

public class C_PlaceSoldier extends DirectlyPlayedCard {
    public C_PlaceSoldier() {
        //todo other picture
        super(7, "Verstärkung", "Jeder Spieler platziert einen Soldat");
    }

    @Override
    protected void play() {
        PlayerUtils.iteratePlayers(game, 1000, player -> {
            placeUnitHandler.placeSoldier(player);
        });
    }

}
