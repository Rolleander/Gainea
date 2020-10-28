package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;

public class G_KillUnits extends AbstractGoal {
    private final int killTarget;
    private int kills = 0;

    public G_KillUnits() {
        this(GoalDifficulty.EASY, 5);
    }

    public G_KillUnits(GoalDifficulty difficulty, int kills) {
        super(difficulty, "Vernichte " + kills + " Soldaten anderer Spieler");
        this.killTarget = kills;
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.getAttacker() == player) {
            kills += result.getDefenders().stream().filter(BattleObject::isDead).filter(it -> it instanceof Soldier).count();
            check();
        }
    }

    @Override
    public void check() {
        if (kills >= killTarget) {
            success();
        }
    }
}
