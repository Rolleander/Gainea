package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.bot.BotOptionalAction;
import com.broll.gainea.server.core.bot.CardOption;
import com.broll.gainea.server.core.bot.strategy.ICardStrategy;
import com.broll.gainea.server.core.cards.Card;

public class BotCard extends BotOptionalAction<NT_Action_Card, CardOption> {

    @Override
    protected void react(NT_Action_Card action, NT_Reaction reaction) {
        BotSelect select = (BotSelect) handler.getActionHandler(BotSelect.class);
        select.clearSelections();
        getSelectedOption().getSelectOptions().forEach(select::nextChooseOption);
    }

    @Override
    public Class<? extends NT_Action> getActionClass() {
        return NT_Action_Card.class;
    }

    @Override
    public CardOption score(NT_Action_Card action) {
        BotSelect select = (BotSelect) handler.getActionHandler(BotSelect.class);
        Card card = bot.getCardHandler().getCards().stream().filter(it -> it.getId() == action.cardId).findFirst().orElse(null);
        if (card instanceof ICardStrategy) {
            return ((ICardStrategy) card).strategy(strategy, select);
        }
        return null;
    }

}
