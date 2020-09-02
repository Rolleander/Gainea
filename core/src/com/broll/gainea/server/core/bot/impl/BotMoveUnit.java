package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_Action_MoveUnit;
import com.broll.gainea.net.NT_PlayerTurn;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.bot.BotAction;

public class BotMoveUnit extends BotAction<NT_Action_MoveUnit> {
    @Override
    public Class<NT_Action_MoveUnit> getActionClass() {
        return NT_Action_MoveUnit.class;
    }

    @Override
    protected void handleAction(NT_Action_MoveUnit action, NT_Reaction reaction) {

    }


    @Override
    public float score(NT_Action_MoveUnit action, NT_PlayerTurn turn) {
        return -1;
    }
}
