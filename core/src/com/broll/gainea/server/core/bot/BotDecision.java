package com.broll.gainea.server.core.bot;

import com.broll.gainea.net.NT_PlayerTurn;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;

public abstract class BotDecision<A, R> {

    protected GameContainer game;
    protected Player bot;

    void init(GameContainer game, Player bot) {
        this.game = game;
        this.bot = bot;
    }

    public abstract float score(A action, NT_PlayerTurn turn);

    public abstract R perform(A action);

}
