package com.broll.gainea.server.core.actions.impl;

import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Event_PlayedCard;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.MessageUtils;
import com.broll.gainea.server.core.utils.ProcessingUtils;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardAction extends AbstractActionHandler<NT_Action_Card, CardAction.Context> {

    class Context extends ActionContext<NT_Action_Card> {
        AbstractCard card;
        Player player;

        public Context(NT_Action_Card action) {
            super(action);
        }
    }

    public Context playableCard(Player player, AbstractCard card) {
        NT_Action_Card action = new NT_Action_Card();
        action.cardId = card.getId();
        Context context = new Context(action);
        context.card = card;
        context.player = player;
        return context;
    }

    @Override
    public void handleReaction(Context context, NT_Action_Card action, NT_Reaction reaction) {
        playCard(context.player, context.card);
    }

    public void playCard(Player player, AbstractCard card) {
        game.getProcessingCore().execute(() -> {
            NT_Event_PlayedCard playedCard = new NT_Event_PlayedCard();
            playedCard.player = player.getServerPlayer().getId();
            playedCard.card = card.nt();
            reactionResult.sendGameUpdate(playedCard);
            MessageUtils.gameLog(game, "Karte " + card.getTitle() + " ausgespielt");
            player.getCardHandler().discardCard(card);
            ProcessingUtils.pause(3000);
            card.play(actionHandlers);
            game.getUpdateReceiver().playedCard(card);
        });
    }

}
