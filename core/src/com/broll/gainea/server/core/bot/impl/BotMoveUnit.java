package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_PlayerTurnStart;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.bot.BotAction;

public class BotMoveUnit extends BotAction<NT_Action_Move> {
    @Override
    public Class<NT_Action_Move> getActionClass() {
        return NT_Action_Move.class;
    }

    @Override
    protected void handleAction(NT_Action_Move action, NT_Reaction reaction) {

    }


    @Override
    public float score(NT_Action_Move action, NT_PlayerTurnActions turn) {
        return -1;
    }
}
