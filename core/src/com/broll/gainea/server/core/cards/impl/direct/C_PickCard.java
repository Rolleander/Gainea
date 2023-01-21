package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class C_PickCard extends DirectlyPlayedCard {

    private final static int OPTIONS = 3;

    public C_PickCard() {
        super(2, "Arkane Bibliothek", "Wählt zwischen " + OPTIONS + " verschiedenen Aktionskarten aus.");
    }

    @Override
    protected void play() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < OPTIONS; i++) {
            cards.add(game.getCardStorage().getRandomCard());
        }
        Card card = cards.get(selectHandler.selection("Wählt eine Karte", cards.stream().map(Card::getTitle).collect(Collectors.toList())));
        owner.getCardHandler().receiveCard(card);
    }

}
