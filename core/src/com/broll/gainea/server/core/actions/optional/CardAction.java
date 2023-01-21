package com.broll.gainea.server.core.actions.optional;

import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Event_PlayedCard;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.MessageUtils;
import com.broll.gainea.server.core.utils.ProcessingUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardAction extends AbstractActionHandler<NT_Action_Card, CardAction.Context> {

    public final static int PLAY_CARD_DELAY = 5000;
    private final static Logger Log = LoggerFactory.getLogger(CardAction.class);

    class Context extends ActionContext<NT_Action_Card> {
        Card card;
        Player player;

        public Context(NT_Action_Card action) {
            super(action);
        }
    }

    public Context playableCard(Player player, Card card) {
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

    public void playCard(Player player, Card card) {
        game.getProcessingCore().execute(() -> {
            Log.trace("Handle card reaction");
            NT_Event_PlayedCard playedCard = new NT_Event_PlayedCard();
            playedCard.player = player.getServerPlayer().getId();
            playedCard.card = card.nt();
            reactionResult.sendGameUpdate(playedCard);
            MessageUtils.gameLog(game, "Karte " + card.getTitle() + " ausgespielt");
            player.getCardHandler().discardCard(card);
            ProcessingUtils.pause(PLAY_CARD_DELAY);
            card.play(actionHandlers);
            game.getUpdateReceiver().playedCard(card);
        });
    }

}
