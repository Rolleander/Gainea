package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.bot.BotAction;

public class BotCard extends BotAction<NT_Action_Card> {
    @Override
    protected void handleAction(NT_Action_Card action, NT_Reaction reaction) {

    }

    @Override
    public Class<NT_Action_Card> getActionClass() {
        return NT_Action_Card.class;
    }

    @Override
    public float score(NT_Action_Card action, NT_PlayerTurnActions turn) {
        return -1;
    }
}
