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
        super(difficulty, "Vernichte " + kills + " Soldaten anderer Spieler durch Kämpfe");
        this.killTarget = kills;
    }

    @Override
    public void killed(BattleObject unit, BattleResult throughBattle) {
        if (throughBattle != null) {
            if (throughBattle.getAttacker() == player) {
                kills += throughBattle.getDefenders().stream().filter(BattleObject::isDead).filter(it -> it instanceof Soldier).count();
                check();
            } else if (throughBattle.getDefenders() == player) {
                kills += throughBattle.getAttackers().stream().filter(BattleObject::isDead).filter(it -> it instanceof Soldier).count();
                check();
            }
        }
    }

    @Override
    public void check() {
        if (kills >= killTarget) {
            success();
        }
    }
}
