package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Soldier;

public class G_WinBattles extends AbstractGoal {
    private final int winTarget;
    private int wins = 0;

    public G_WinBattles() {
        this(GoalDifficulty.MEDIUM, 5);
    }

    public G_WinBattles(GoalDifficulty difficulty, int wins) {
        super(difficulty, "Gewinne " + wins + " Schlachten");
        this.winTarget = wins;
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.getWinnerPlayer() == player) {
            wins++;
            check();
        }
    }

    @Override
    public void check() {
        if (wins >= winTarget) {
            success();
        }
    }
}
