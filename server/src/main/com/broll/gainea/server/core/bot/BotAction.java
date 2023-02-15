package com.broll.gainea.server.core.bot;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Reaction;

public abstract class BotAction<A extends NT_Action> extends BotDecision<A, NT_Reaction> {


    protected abstract void handleAction(A action, NT_Reaction reaction);

    @Override
    public NT_Reaction perform(A action) {
        NT_Reaction reaction = new NT_Reaction();
        reaction.actionId = action.actionId;
        handleAction(action, reaction);
        return reaction;
    }

    public abstract Class<A> getActionClass();
}
