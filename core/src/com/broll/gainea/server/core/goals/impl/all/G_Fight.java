package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.RoundGoal;

public class G_Fight extends RoundGoal {

    private boolean fighting = false;

    public G_Fight() {
        super(GoalDifficulty.EASY, "Sei für 5 aufeinanderfolgende Runden an Kämpfen beteiligt", 5);
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.getAttacker() == player || result.getDefender() == player) {
            fighting = true;
        }
    }

    @Override
    public void check() {
        if (fighting) {
            progressRound();
        } else {
            resetRounds();
        }
        fighting = false;
    }
}
