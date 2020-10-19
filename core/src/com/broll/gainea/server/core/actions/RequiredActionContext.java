package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_PlayerAction;

public class RequiredActionContext<T extends NT_Action> extends ActionContext<T> {

    private ActionContext<T> actionContext;
    private String text;
    private Object messageForOtherPlayer;

    public RequiredActionContext(ActionContext<T> actionContext, String text) {
        this(actionContext, text, null);
    }

    public RequiredActionContext(ActionContext<T> actionContext, String text, Object messageForOtherPlayers) {
        super(actionContext.getAction());
        this.actionContext = actionContext;
        this.text = text;
        this.messageForOtherPlayer = messageForOtherPlayers;
    }

    public Object getMessageForOtherPlayer() {
        return messageForOtherPlayer;
    }

    public NT_PlayerAction nt() {
        NT_PlayerAction action = new NT_PlayerAction();
        action.action = actionContext.getAction();
        action.text = text;
        return action;
    }

    public ActionContext<T> getActionContext() {
        return actionContext;
    }
}
