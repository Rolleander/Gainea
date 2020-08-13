package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_PlayerTurn;
import com.broll.gainea.server.core.bot.BotDecision;

public class EndTurnDecision extends BotDecision<Object, NT_EndTurn> {

    @Override
    public float score(Object action, NT_PlayerTurn turn) {
        return 0;
    }

    @Override
    public NT_EndTurn perform(Object action) {
        return new NT_EndTurn();
    }
}
