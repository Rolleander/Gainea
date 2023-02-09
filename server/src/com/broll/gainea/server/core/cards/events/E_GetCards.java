package com.broll.gainea.server.core.cards.events;

import com.broll.gainea.server.core.cards.EventCard;

public class E_GetCards extends EventCard {
    public E_GetCards() {
        super(35, "Markttag", "Jeder Spieler erhält eine Karte");
    }

    @Override
    protected void play() {
        game.getPlayers().forEach(player -> {
            player.getCardHandler().receiveCard(game.getCardStorage().getRandomPlayableCard());
        });
    }

}
