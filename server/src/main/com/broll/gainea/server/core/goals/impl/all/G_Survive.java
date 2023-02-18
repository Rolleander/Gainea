package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.RoundGoal;
import com.broll.gainea.server.core.objects.BattleObject;

public class G_Survive extends RoundGoal {
    private final static int TARGET = 5;

    public G_Survive() {
        super(GoalDifficulty.EASY, "Verliere für " + TARGET + " Runden keine Einheit", TARGET);
    }

    @Override
    public void killed(BattleObject unit, BattleResult throughBattle) {
        if (unit.getOwner() == player) {
            resetRounds();
        }
    }

    @Override
    public void check() {
        progressRound();
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        //todo
    }
}
