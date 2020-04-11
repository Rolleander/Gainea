package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Action;

public class NoActionContext<T extends NT_Action> extends ActionContext<T> {

    public NoActionContext(T action) {
        super(action);
    }

    public NoActionContext(T action, CustomReactionHandler<NoActionContext> handler) {
        this(action);
        this.setCustomHandler(handler);
    }

}
