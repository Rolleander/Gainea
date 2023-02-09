package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Soldier;

public class G_KillUnits extends Goal {
    private final int killTarget;
    private int kills = 0;

    public G_KillUnits() {
        this(GoalDifficulty.EASY, 6);
    }

    public G_KillUnits(GoalDifficulty difficulty, int kills) {
        super(difficulty, "Vernichte " + kills + " Soldaten anderer Spieler durch Kämpfe");
        this.killTarget = kills;
        setProgressionGoal(kills);
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.getAttackingPlayer() == player) {
            kills += result.getKilledDefenders().stream().filter(it -> it instanceof Soldier).count();
            check();
        } else if (result.getDefendingPlayer() == player) {
            kills += result.getKilledAttackers().stream().filter(it -> it instanceof Soldier).count();
            check();
        }
    }

    @Override
    public void check() {
        updateProgression(kills);
        if (kills >= killTarget) {
            success();
        }
    }
}
