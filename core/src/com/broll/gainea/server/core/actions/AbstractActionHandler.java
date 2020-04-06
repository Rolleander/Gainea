package com.broll.gainea.server.core.actions;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Reaction;

public abstract class AbstractActionHandler<T extends NT_Action, C> {

    protected GameContainer game;
    protected Player player;
    protected ReactionActions reactionResult;
    protected ActionHandlers actionHandlers;
    protected boolean keepAction;

    public void init(GameContainer game, ReactionActions reactionResult, ActionHandlers actionHandlers) {
        this.game = game;
        this.reactionResult = reactionResult;
        this.actionHandlers = actionHandlers;
    }

    public void update(Player player) {
        this.keepAction = false;
        this.player = player;
    }

    protected void keepAction() {
        keepAction = true;
    }

    public boolean isKeepAction() {
        return keepAction;
    }

    public abstract void handleReaction(C context, T action, NT_Reaction reaction);
}
