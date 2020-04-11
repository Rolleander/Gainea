package com.broll.gainea.server.core.actions.impl;

import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;

import java.util.List;

public class CardAction extends AbstractActionHandler<NT_Action_Card, CardAction.Context> {

    class Context extends ActionContext<NT_Action_Card> {
        AbstractCard card;

        public Context(NT_Action_Card action) {
            super(action);
        }
    }

    public Context playableCard(AbstractCard card) {
        NT_Action_Card action = new NT_Action_Card();
        action.cardId = card.getId();
        Context context = new Context(action);
        context.card = card;
        return context;
    }

    @Override
    public void handleReaction(Context context, NT_Action_Card action, NT_Reaction reaction) {
        playCard(context.card);
    }

    public void playCard(AbstractCard card){
        card.play(actionHandlers);
        player.getCardHandler().discardCard(card);
    }
}
