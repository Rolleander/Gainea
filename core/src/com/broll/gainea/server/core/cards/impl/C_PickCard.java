package com.broll.gainea.server.core.cards.impl;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class C_PickCard extends DirectlyPlayedCard {

    private final static int OPTIONS = 3;

    public C_PickCard() {
        super(2, "Arkane Bibliothek", "Wählt zwischen " + OPTIONS + " verschiedenen Aktionskarten aus.");
    }

    @Override
    protected void play() {
        List<AbstractCard> cards = new ArrayList<>();
        for (int i = 0; i < OPTIONS; i++) {
            cards.add(game.getCardStorage().getRandomCard());
        }
        AbstractCard card = cards.get(selectHandler.selection("Wählt eine Karte", cards.stream().map(AbstractCard::getTitle).collect(Collectors.toList())));
        owner.getCardHandler().receiveCard(card);
    }

}
