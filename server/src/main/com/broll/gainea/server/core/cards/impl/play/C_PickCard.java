package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;

import java.util.List;
import java.util.stream.Collectors;

public class C_PickCard extends Card {

    private final static int OPTIONS = 3;

    public C_PickCard() {
        super(2, "Arkane Bibliothek", "Wählt zwischen " + OPTIONS + " verschiedenen Aktionskarten aus.");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        List<Card> cards = game.getCardStorage().getPlayableCards(OPTIONS);
        Card card = cards.get(selectHandler.selectObject("Wählt eine Karte", cards.stream().map(Card::nt).collect(Collectors.toList())));
        owner.getCardHandler().receiveCard(card);
    }

}
