package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_StealCard extends Card {
    public C_StealCard() {
        super(13, "Bekehrung", "Übernehmt eine Aktionskarte von einem beliebigen Spieler");
        setDrawChance(0.5f);
    }

    @Override
    public boolean isPlayable() {
        return !getStealTargets().isEmpty();
    }

    private List<Player> getStealTargets() {
        return PlayerUtils.getOtherPlayers(game, owner).filter(it -> !it.getCardHandler().getCards().isEmpty()).collect(Collectors.toList());
    }

    @Override
    protected void play() {
        Player player = selectHandler.selectPlayer(owner, getStealTargets(), "Welchen Spieler bekehren?");
        List<Card> cards = player.getCardHandler().getCards();
        Card card = cards.get(selectHandler.selectObject("Wählt eine Karte", cards.stream().map(Card::nt).collect(Collectors.toList())));
        player.getCardHandler().discardCard(card);
        owner.getCardHandler().receiveCard(card);
    }

}
