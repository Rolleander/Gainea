package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;

public class C_Treasury extends Card {


    public C_Treasury() {
        super(3, "Reichtum", "Erhaltet ein weiteres Ziel");
        setDrawChance(0.5f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
       game.getGoalStorage().assignNewRandomGoal(owner);
    }

}
