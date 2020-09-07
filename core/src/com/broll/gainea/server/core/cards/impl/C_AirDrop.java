package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.actions.impl.PlaceUnitAction;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.stream.Collectors;

public class C_AirDrop extends DirectlyPlayedCard {
    public C_AirDrop() {
        super("Verstärkung", "Jeder Spieler platziert eine Einheit!");
    }

    @Override
    public void play() {
        PlayerUtils.iteratePlayers(game, 1000, player -> {
            placeUnitHandler.placeSoldier(player.getControlledLocations());
        });
    }

}
