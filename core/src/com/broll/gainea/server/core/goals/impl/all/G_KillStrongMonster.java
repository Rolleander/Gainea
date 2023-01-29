package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;

public class G_KillStrongMonster extends Goal {

    public G_KillStrongMonster() {
        super(GoalDifficulty.MEDIUM, "Töte ein Monster mit 4 oder mehr Sternen mit nur einer Einheit");
    }

    @Override
    public void killed(BattleObject unit, BattleResult throughBattle) {
        if (throughBattle != null && throughBattle.getAttacker() == player && throughBattle.getAttackers().size() == 1 &&
                throughBattle.getKilledDefenders().stream().filter(it -> it instanceof Monster).map(it -> (Monster) it)
                        .anyMatch(it -> it.getStars() >= 4 && it.getOwner() == null)) {
            success();
        }
    }

    @Override
    public void check() {

    }
}
