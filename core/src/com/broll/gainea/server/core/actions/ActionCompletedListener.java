package com.broll.gainea.server.core.actions;

public interface ActionCompletedListener<T extends ActionContext> {

    void completed(T context);
}
