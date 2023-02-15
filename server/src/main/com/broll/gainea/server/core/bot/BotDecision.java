package com.broll.gainea.server.core.bot;

import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.bot.strategy.BotStrategy;
import com.broll.gainea.server.core.player.Player;

public abstract class BotDecision<A, R> {

    protected GameContainer game;
    protected Player bot;
    protected BotStrategy strategy;

    void init(GameContainer game, Player bot, BotStrategy strategy) {
        this.game = game;
        this.bot = bot;
        this.strategy = strategy;
    }

    public abstract float score(A action, NT_PlayerTurnActions turn);

    public abstract R perform(A action);

}
