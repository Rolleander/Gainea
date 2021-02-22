package com.broll.gainea.server.core.bot;

import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_PlayerTurnStart;

public class EndTurnDecision extends BotDecision<Object, NT_EndTurn> {

    @Override
    public float score(Object action, NT_PlayerTurnActions turn) {
        return 0;
    }

    @Override
    public NT_EndTurn perform(Object action) {
        return new NT_EndTurn();
    }
}
