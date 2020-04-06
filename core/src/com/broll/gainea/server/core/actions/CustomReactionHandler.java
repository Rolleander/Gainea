package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Reaction;

public interface CustomReactionHandler<T extends ActionContext> {
    
    void handleReaction(T context, NT_Reaction reaction);
}
