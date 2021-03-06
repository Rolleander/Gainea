package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;

public class G_KillCommander extends AbstractGoal {

    public G_KillCommander() {
        super(GoalDifficulty.EASY, "Besiege den Feldherr eines anderen Spielers im Kampf");
    }

    @Override
    public void killed(BattleObject unit, BattleResult throughBattle) {
        if (throughBattle != null) {
            if (throughBattle.getAttacker() == player && throughBattle.getDefenders().stream().anyMatch(it -> it instanceof Commander && it.isDead())) {
                success();
            } else if (throughBattle.getDefenders() == player && throughBattle.getAttackers().stream().anyMatch(it -> it instanceof Commander && it.isDead())) {
                success();
            }
        }
    }

    @Override
    public void check() {

    }
}
