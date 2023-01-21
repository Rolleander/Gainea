package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Event_RemoveCard;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.player.Player;

import java.util.List;
import java.util.stream.Collectors;

public class C_StealCard extends Card {
    public C_StealCard() {
        super(13, "Bekehrung", "Übernehmt eine Aktionskarte von einem beliebigen Spieler");
        setDrawChance(0.5f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Player player = selectHandler.selectOtherPlayer(owner, "Welchen Spieler bekehren?");
        List<Card> cards = player.getCardHandler().getCards();
        if (!cards.isEmpty()) {
            Card card = cards.get(selectHandler.selection("Wählt eine Karte", cards.stream().map(Card::getTitle).collect(Collectors.toList())));
            player.getCardHandler().discardCard(card);
            card.init(game, owner, card.getId());
            owner.getCardHandler().receiveCard(card);
            //update stolen player to remove his card
            NT_Event_RemoveCard nt = new NT_Event_RemoveCard();
            nt.card = card.nt();
            player.getServerPlayer().sendTCP(nt);
        }
    }

}
