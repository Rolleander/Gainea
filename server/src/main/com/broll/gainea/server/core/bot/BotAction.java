package com.broll.gainea.server.core.bot;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.bot.strategy.BotStrategy;
import com.broll.gainea.server.core.player.Player;

public abstract class BotAction<A extends NT_Action> {

    protected GameContainer game;
    protected Player bot;
    protected BotStrategy strategy;
    protected BotActionHandler handler;

    void init(GameContainer game, Player bot, BotStrategy strategy, BotActionHandler handler) {
        this.game = game;
        this.bot = bot;
        this.strategy = strategy;
        this.handler = handler;
    }

    protected abstract void react(A action, NT_Reaction reaction);

    public NT_Reaction perform(A action) {
        NT_Reaction reaction = new NT_Reaction();
        reaction.actionId = action.actionId;
        react(action, reaction);
        return reaction;
    }

    public abstract Class<? extends NT_Action> getActionClass();
}
