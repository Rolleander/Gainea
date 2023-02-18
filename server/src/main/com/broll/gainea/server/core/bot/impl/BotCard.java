package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.bot.BotOptionalAction;

public class BotCard extends BotOptionalAction<NT_Action_Card, BotCard.CardOption> {

    @Override
    protected void react(NT_Action_Card action, NT_Reaction reaction) {

    }

    @Override
    public Class<? extends NT_Action> getActionClass() {
        return NT_Action_Card.class;
    }

    @Override
    public CardOption score(NT_Action_Card action) {
        return null;
    }

    public static class CardOption extends BotOptionalAction.BotOption {

        public CardOption(float score) {
            super(score);
        }
    }
}
