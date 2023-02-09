package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;

public class G_WinBattles extends Goal {
    private final int winTarget;
    private int wins = 0;

    public G_WinBattles() {
        this(GoalDifficulty.MEDIUM, 6);
    }

    public G_WinBattles(GoalDifficulty difficulty, int wins) {
        super(difficulty, "Gewinne " + wins + " Schlachten gegen andere Spieler");
        this.winTarget = wins;
        setProgressionGoal(wins);
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.getWinnerPlayer() == player && result.getLoserPlayer() != null) {
            wins++;
            check();
        }
    }

    @Override
    public void check() {
        updateProgression(wins);
        if (wins >= winTarget) {
            success();
        }
    }
}
