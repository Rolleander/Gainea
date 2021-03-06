package com.broll.gainea.server.core.actions;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.processing.ProcessingBlock;

public abstract class AbstractActionHandler<T extends NT_Action, C> {

    protected GameContainer game;
    protected Player player;
    protected ReactionActions reactionResult;
    protected ActionHandlers actionHandlers;
    protected ProcessingBlock processingBlock = new ProcessingBlock();

    public void init(GameContainer game, ReactionActions reactionResult, ActionHandlers actionHandlers) {
        this.game = game;
        this.reactionResult = reactionResult;
        this.actionHandlers = actionHandlers;
    }

    public void update(Player player) {
        this.player = player;
    }

    public abstract void handleReaction(C context, T action, NT_Reaction reaction);

}
