package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Action;

public abstract class ActionContext<T extends NT_Action> {
    private T action;
    private CustomReactionHandler customHandler;
    private ActionCompletedListener completionListener;

    public ActionContext(T action){
        this.action = action;
    }

    public void setCompletionListener(ActionCompletedListener completionListener) {
        this.completionListener = completionListener;
    }

    public void setCustomHandler(CustomReactionHandler customHandler) {
        this.customHandler = customHandler;
    }

    public ActionCompletedListener getCompletionListener() {
        return completionListener;
    }

    public CustomReactionHandler getCustomHandler() {
        return customHandler;
    }

    public T getAction() {
        return action;
    }
}
