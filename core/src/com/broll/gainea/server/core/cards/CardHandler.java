package com.broll.gainea.server.core.cards;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.TurnBuilder;
import com.broll.gainea.server.core.actions.impl.CardAction;
import com.broll.gainea.server.core.player.Player;

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
        cards.stream().filter(AbstractCard::isPlayable).forEach(card -> builder.action(cardAction.playableCard(card)));
    }

    public void receiveCard(AbstractCard card) {
        if (card instanceof DirectlyPlayedCard) {
            game.getReactionHandler().getActionHandlers().getHandler(CardAction.class).playCard(card);
            return;
        }
        this.cards.add(card);
    }

    public void discardCard(AbstractCard card) {
        this.cards.remove(card);
    }

    public int getCardCount() {
        return this.cards.size();
    }
}
