package com.broll.gainea.server.core.cards;

import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_Event_DrawedCard;
import com.broll.gainea.net.NT_Event_TextInfo;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.TurnBuilder;
import com.broll.gainea.server.core.actions.impl.CardAction;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.MessageUtils;

import java.util.List;
import java.util.ArrayList;

public class CardHandler {

    private List<AbstractCard> cards = new ArrayList<>();
    private GameContainer game;
    private Player player;

    public CardHandler(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void drawRandomCard() {
        game.getCardStorage().drawRandomCard(player);
    }

    public void onTurnStart(TurnBuilder builder, ActionHandlers actionHandlers) {
        CardAction cardAction = actionHandlers.getHandler(CardAction.class);
        cards.stream().filter(AbstractCard::isPlayable).forEach(card -> builder.action(cardAction.playableCard(player,card)));
    }

    public void receiveCard(AbstractCard card) {
        if (card instanceof DirectlyPlayedCard) {
            game.getReactionHandler().getActionHandlers().getHandler(CardAction.class).playCard(player,card);
            return;
        }
        this.cards.add(card);
        NT_Event_DrawedCard nt = new NT_Event_DrawedCard();
        nt.card = card.nt();
        player.getServerPlayer().sendTCP(nt);
        MessageUtils.gameLog(game, player.getServerPlayer().getName() + " hat eine Karte erhalten");
    }

    public void discardCard(AbstractCard card) {
        this.cards.remove(card);
    }

    public int getCardCount() {
        return this.cards.size();
    }

    public NT_Card[] ntCards() {
        return cards.stream().map(AbstractCard::nt).toArray(NT_Card[]::new);
    }
}
